package com.amarple.expense.cli

import com.amarple.expense.category.AggregateBy
import com.amarple.expense.category.AggregateByCategory
import com.amarple.expense.category.AggregateByCategoryAndInstrument
import com.amarple.expense.category.AggregateByInstrument
import com.amarple.expense.category.AggregateNothing
import com.amarple.expense.category.AmExCategoryCategorizer
import com.amarple.expense.category.BespokeCategorizer
import com.amarple.expense.category.CapitalOneCategoryCategorizer
import com.amarple.expense.category.ChaseCategoryCategorizer
import com.amarple.expense.category.DescriptionPatternCategorizer
import com.amarple.expense.category.DiscoverCategoryCategorizer
import com.amarple.expense.category.StaticCategorizer
import com.amarple.expense.expected.DescriptionPatternExpectedExpenseMatcher
import com.amarple.expense.model.AggregationType
import com.amarple.expense.model.DateRange
import com.amarple.expense.model.ImportInput
import com.amarple.expense.model.ImportOutput
import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.ExpectedExpense
import com.amarple.expense.model.internal.ExpenseReport
import com.amarple.expense.model.internal.PAYMENTS
import com.amarple.expense.model.internal.REWARDS
import com.amarple.expense.model.internal.TRANSFERS
import com.amarple.expense.model.internal.Transaction
import com.amarple.expense.read.config.AmExCategoryReader
import com.amarple.expense.read.config.CapitalOneCategoryReader
import com.amarple.expense.read.config.ChaseCategoryReader
import com.amarple.expense.read.config.DescriptionPatternCategoryReader
import com.amarple.expense.read.config.DescriptionPatternExpectedExpenseReader
import com.amarple.expense.read.config.DiscoverCategoryReader
import com.amarple.expense.read.expected.ExpectedExpenseReader
import com.amarple.expense.read.expected.UberSheetExpectedExpenseReader
import com.amarple.expense.read.report.AmExExpenseReportReader
import com.amarple.expense.read.report.BoaExpenseReportReader
import com.amarple.expense.read.report.CapitalOneExpenseReportReader
import com.amarple.expense.read.report.ChaseExpenseReportReader
import com.amarple.expense.read.report.DiscoverExpenseReportReader
import com.amarple.expense.read.report.ExpenseReportReaderSelector
import com.amarple.expense.read.report.ExpenseReportType
import com.amarple.expense.read.report.WellsFargoExpenseReportReader
import java.time.LocalDate
import java.time.YearMonth
import kotlin.collections.firstOrNull
import kotlin.collections.forEachIndexed

class ImportTask(
    private val expectedExpenseReader: ExpectedExpenseReader = UberSheetExpectedExpenseReader(),
    private val amExCategoryCategoryReader: AmExCategoryReader = AmExCategoryReader(),
    private val capitalOneCategoryReader: CapitalOneCategoryReader = CapitalOneCategoryReader(),
    private val chaseCategoryReader: ChaseCategoryReader = ChaseCategoryReader(),
    private val discoverCategoryReader: DiscoverCategoryReader = DiscoverCategoryReader(),
    private val patternCategoryReader: DescriptionPatternCategoryReader = DescriptionPatternCategoryReader(),
    private val patternExpectedExpenseReader: DescriptionPatternExpectedExpenseReader = DescriptionPatternExpectedExpenseReader(),
    private val expenseReportReaderSelector: ExpenseReportReaderSelector = ExpenseReportReaderSelector(
        mapOf(
            ExpenseReportType.Discover to DiscoverExpenseReportReader(),
            ExpenseReportType.BankOfAmerica to BoaExpenseReportReader(),
            ExpenseReportType.WellsFargo to WellsFargoExpenseReportReader(),
            ExpenseReportType.AmericanExpress to AmExExpenseReportReader(),
            ExpenseReportType.CapitalOne to CapitalOneExpenseReportReader(),
            ExpenseReportType.Chase to ChaseExpenseReportReader(),
        )
    ),
) {
    fun execute(importInput: ImportInput): ImportOutput {
        // 1. Read the expected expenses using the classes in com.amarple.expense.read.expected
        val expectedExpenses = expectedExpenseReader.read(importInput.expectedExpenses)

        // 1.a. Read the description patterns, which will be used when matching expected expenses
        val expectedExpensePatterns = patternExpectedExpenseReader.read(importInput.expectedExpenses.descriptionPatterns)

        // 2. Read the configuration for categories, which will be used when categorizing expenses
        val categoryPatterns = patternCategoryReader.read(importInput.categories.descriptionPatterns)
        val amExCategories = importInput.categories.amExCategories
            ?.let { amExCategoryCategoryReader.read(it) }
            ?: emptyList()
        val capitalOneCategories = importInput.categories.capitalOneCategories
            ?.let { capitalOneCategoryReader.read(it) }
            ?: emptyList()
        val chaseCategories = importInput.categories.chaseCategories
            ?.let { chaseCategoryReader.read(it) }
            ?: emptyList()
        val discoverCategories = importInput.categories.discoverCategories
            ?.let { discoverCategoryReader.read(it) }
            ?: emptyList()

        // 3. Read the reports using the classes in com.amarple.expense.read.report
        val expenseReports = readExpenseReports(importInput)
        val allTransactions: List<Transaction<*>> = expenseReports
            .flatMap { it.transactions }
            .filter { transaction ->
                importInput.aggregation.dateRange?.let { dateRange ->
                    isInDateRange(transaction.date, dateRange)
                } ?: true
            }

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
            listOf(
                DiscoverCategoryCategorizer(discoverCategories),
                AmExCategoryCategorizer(amExCategories),
                CapitalOneCategoryCategorizer(capitalOneCategories),
                ChaseCategoryCategorizer(chaseCategories),
            ),
            DescriptionPatternCategorizer(categoryPatterns),
            StaticCategorizer(Category("Entertainment", "Miscellaneous"))
        )
        // 5.c. TODO: find a way to categorize incoming deposits and outgoing transfers so that we can return them separately

        // 6. Calculate new AggregatedTransactions
        val aggregator: AggregateBy = when (importInput.aggregation.aggregationType) {
            AggregationType.Nothing -> AggregateNothing
            AggregationType.Category -> AggregateByCategory
            AggregationType.Instrument -> AggregateByInstrument
            AggregationType.CategoryAndInstrument -> AggregateByCategoryAndInstrument
        }
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
        // 6.b. From transactions, exclude ...
        val transactionsToAggregate = categorizedTransactions
            .filter { it.category != PAYMENTS } // 6.b.1. payments, TODO: add a check that the total of payments across all instruments is 0, i.e. positive credits on cards negates payments from a bank account
            .filter { it.category != REWARDS } // 6.b.2. rewards, TODO: return rewards so they can be included in income
            .filter { it.category != TRANSFERS } // 6.b.3. and incoming deposits TODO: remove this line after adding a better way to filter out incoming deposits
        // 6.c. ... and group transactions by category and instrument
        val aggregatedTransactions = aggregator.aggregate(transactionsToAggregate)

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

    fun isInDateRange(date: LocalDate, dateRange: DateRange): Boolean {
        return dateRange.startDate <= date && date <= dateRange.endDate
    }
}