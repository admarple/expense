package com.amarple.expense.cli

import com.amarple.expense.category.AggregateBy
import com.amarple.expense.category.AggregateByCategoryAndInstrument
import com.amarple.expense.category.AmExCategoryCategorizer
import com.amarple.expense.category.BespokeCategorizer
import com.amarple.expense.category.DescriptionPatternCategorizer
import com.amarple.expense.category.DiscoverCategoryCategorizer
import com.amarple.expense.category.StaticCategorizer
import com.amarple.expense.expected.DescriptionPatternExpectedExpenseMatcher
import com.amarple.expense.model.ImportInput
import com.amarple.expense.model.ImportOutput
import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.ExpectedExpense
import com.amarple.expense.model.internal.ExpenseReport
import com.amarple.expense.model.internal.Transaction
import com.amarple.expense.read.config.AmExCategoryReader
import com.amarple.expense.read.config.DescriptionPatternCategoryReader
import com.amarple.expense.read.config.DescriptionPatternExpectedExpenseReader
import com.amarple.expense.read.expected.ExpectedExpenseReader
import com.amarple.expense.read.expected.UberSheetExpectedExpenseReader
import com.amarple.expense.read.report.AmExExpenseReportReader
import com.amarple.expense.read.report.BoaExpenseReportReader
import com.amarple.expense.read.report.DiscoverExpenseReportReader
import com.amarple.expense.read.report.ExpenseReportReaderSelector
import com.amarple.expense.read.report.ExpenseReportType
import com.amarple.expense.read.report.WellsFargoExpenseReportReader
import java.time.YearMonth
import kotlin.collections.firstOrNull
import kotlin.collections.forEachIndexed

class ImportTask(
    private val expectedExpenseReader: ExpectedExpenseReader = UberSheetExpectedExpenseReader(),
    private val amExCategoryCategoryReader: AmExCategoryReader = AmExCategoryReader(),
    private val patternCategoryReader: DescriptionPatternCategoryReader = DescriptionPatternCategoryReader(),
    private val patternExpectedExpenseReader: DescriptionPatternExpectedExpenseReader = DescriptionPatternExpectedExpenseReader(),
    private val expenseReportReaderSelector: ExpenseReportReaderSelector = ExpenseReportReaderSelector(
        mapOf(
            ExpenseReportType.Discover to DiscoverExpenseReportReader(),
            ExpenseReportType.BankOfAmerica to BoaExpenseReportReader(),
            ExpenseReportType.WellsFargo to WellsFargoExpenseReportReader(),
            ExpenseReportType.AmericanExpress to AmExExpenseReportReader(),
        )
    ),
    private val aggregator: AggregateBy = AggregateByCategoryAndInstrument
) {
    fun execute(importInput: ImportInput): ImportOutput {
        // 1. Read the expected expenses using the classes in com.amarple.expense.read.expected
        val expectedExpenses = expectedExpenseReader.read(importInput.expectedExpenses)

        // 1.a. Read the description patterns, which will be used when matching expected expenses
        val expectedExpensePatterns = patternExpectedExpenseReader.read(importInput.expectedExpenses.descriptionPatterns)

        // 2. Read the configuration for categories, which will be used when categorizing expenses
        val categoryPatterns = patternCategoryReader.read(importInput.categories.descriptionPatterns)
        val amExCategories = importInput.categories.amExCategories
            ?.let { amExCategoryCategoryReader.read(importInput.categories.amExCategories) }
            ?: emptyList()

        // 3. Read the reports using the classes in com.amarple.expense.read.report
        val expenseReports = readExpenseReports(importInput)
        val allTransactions: List<Transaction<*>> = expenseReports.flatMap { it.transactions }

        // 4. Try to match transactions from the reports to expected expenses
        val expectedExpenseMatcher = DescriptionPatternExpectedExpenseMatcher(expectedExpensePatterns)
        val matchedTransactions = mutableMapOf<Int, MutableList<Transaction<*>>>() // Index of expected expense to transactions
        val transactionToMatchedExpectedIndexes = mutableMapOf<Transaction<*>, MutableList<Int>>()

        expectedExpenses.forEachIndexed { index, expectedExpense: ExpectedExpense<*> ->
            expectedExpenseMatcher.match(expectedExpense, allTransactions).forEach { transaction: Transaction<*> ->
                matchedTransactions.getOrPut(index) { mutableListOf() }.add(transaction)
                transactionToMatchedExpectedIndexes.getOrPut(transaction) { mutableListOf() }.add(index)
            }
        }

        // 5. Try to categorize transactions from the reports
        val categorizer = BespokeCategorizer(
            DiscoverCategoryCategorizer(),
            AmExCategoryCategorizer(amExCategories),
            DescriptionPatternCategorizer(categoryPatterns),
            StaticCategorizer(Category("Entertainment", "Miscellaneous"))
        )
        // 5.a. TODO: configure the logic for categorizing transactions from each report, e.g. DiscoverCategoryCategorizer can only be used for Discover reports
        // 5.b. TODO: find a way to categorize auto-payments so that we can exclude them
        // 5.c. TODO: find a way to categorize incoming deposits and outgoing transfers so that we can return them separately

        // 6. Calculate new AggregatedTransactions
        // 6.a. Exclude transactions that have already been matched to expected expenses ...
        val unmatchedTransactions = allTransactions.filter { !transactionToMatchedExpectedIndexes.containsKey(it) }
        val categorizedTransactions = unmatchedTransactions.map { transaction: Transaction<*> ->
            val category = categorizer.categorize(transaction)
            if (category != null) {
                transaction.updateCategory(category = category)
            } else {
                transaction
            }
        }
        // 6.b. ... and group transactions by category and instrument
        val aggregatedTransactions = aggregator.aggregate(categorizedTransactions)

        // 7. Build the response, including ...
        // 7.a. ... a list of transactions for expected expenses, in the same order as config, with null to designate expenses where no transaction was matched
        val expectedResults = expectedExpenses.mapIndexed { index, expense ->
            matchedTransactions[index]?.firstOrNull()
                ?.updateCategory(category = expense.expectedTransaction.category)
                ?.let {
                    // This is the point at which the Transaction from the report is dropped
                    // TODO: should we preserve the transaction from the report?
                    BasicTransaction(
                        description = expense.name,
                        amount = it.amount,
                        date = it.date,
                        category = it.category,
                        instrument = it.instrument,
                    )
                }
        }

        // 7.e. ... warnings for ...
        val warnings = mutableListOf<String>()
        // 7.e.2. ... any expected expenses that matched multiple transactions
        matchedTransactions.forEach { (index, transactions) ->
            if (transactions.size > 1) {
                warnings.add("Expected expense '${expectedExpenses[index].name}' matched ${transactions.size} transactions")
            }
        }
        // 7.e.3. ... any transactions that matched multiple expected expenses
        transactionToMatchedExpectedIndexes.forEach { (transaction, indexes) ->
            if (indexes.size > 1) {
                val expenseNames = indexes.joinToString { expectedExpenses[it].name }
                warnings.add("Transaction '${transaction.description}' matched multiple expected expenses: $expenseNames")
            }
        }

        return ImportOutput(
            expectedExpenses = expectedResults,
            categorizedExpenses = aggregatedTransactions,
            outgoingTransfers = emptyList(), // 7.c. TODO: ... a list of transactions for outgoing transfers
            incomingDeposits = emptyList(), // 7.d. TODO: ... a list of transactions for incoming deposits or rewards
            warnings = warnings,
            month = expenseReports.map { YearMonth.from(it.retrievalDate) }.firstOrNull() // 7.f
        )
    }

    fun readExpenseReports(importInput: ImportInput): List<ExpenseReport<*>> {
        return importInput.reports.map {
            val reportType = it.reportType
            val reportReader = expenseReportReaderSelector.getReader(reportType)
            reportReader?.read(it) ?: throw IllegalArgumentException("No report reader found for report type: $reportType")
        }
    }
}