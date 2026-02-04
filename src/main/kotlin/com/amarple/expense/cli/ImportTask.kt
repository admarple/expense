package com.amarple.expense.cli

import com.amarple.expense.category.AggregateBy
import com.amarple.expense.category.AggregationSelector
import com.amarple.expense.category.AmExCategoryCategorizer
import com.amarple.expense.category.BespokeCategorizer
import com.amarple.expense.category.CapitalOneCategoryCategorizer
import com.amarple.expense.category.ChaseCategoryCategorizer
import com.amarple.expense.category.DescriptionPatternCategorizer
import com.amarple.expense.category.DiscoverCategoryCategorizer
import com.amarple.expense.category.DiscretionaryTransactionSorter
import com.amarple.expense.category.DiscretionaryTransactionsBeautifier
import com.amarple.expense.category.StaticCategorizer
import com.amarple.expense.common.DescriptionPermuter
import com.amarple.expense.expected.DescriptionPatternExpectedExpenseMatcher
import com.amarple.expense.model.AggregationType
import com.amarple.expense.model.DateRange
import com.amarple.expense.model.ImportInput
import com.amarple.expense.model.ImportOutput
import com.amarple.expense.model.internal.AggregatedTransaction
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.DEPOSITS
import com.amarple.expense.model.internal.ExpectedExpense
import com.amarple.expense.model.internal.ExpenseReport
import com.amarple.expense.model.internal.PAYMENTS
import com.amarple.expense.model.internal.REWARDS
import com.amarple.expense.model.internal.TRANSFERS
import com.amarple.expense.model.internal.Transaction
import com.amarple.expense.read.config.AmExCategoryReader
import com.amarple.expense.read.config.CapitalOneCategoryReader
import com.amarple.expense.read.config.CategoryHierarchyReader
import com.amarple.expense.read.config.ChaseCategoryReader
import com.amarple.expense.read.config.DescriptionPatternCategoryReader
import com.amarple.expense.read.config.DescriptionPatternExpectedExpenseReader
import com.amarple.expense.read.config.DiscoverCategoryReader
import com.amarple.expense.read.expected.ExpectedExpenseReader
import com.amarple.expense.read.report.ExpenseReportReaderSelector
import java.time.LocalDate
import java.time.YearMonth
import kotlin.collections.firstOrNull
import kotlin.collections.forEachIndexed

class ImportTask(
    private val expectedExpenseReader: ExpectedExpenseReader,
    private val amExCategoryCategoryReader: AmExCategoryReader,
    private val capitalOneCategoryReader: CapitalOneCategoryReader,
    private val chaseCategoryReader: ChaseCategoryReader,
    private val discoverCategoryReader: DiscoverCategoryReader,
    private val categoryHierarchyReader: CategoryHierarchyReader,
    private val patternCategoryReader: DescriptionPatternCategoryReader,
    private val patternExpectedExpenseReader: DescriptionPatternExpectedExpenseReader,
    private val expenseReportReaderSelector: ExpenseReportReaderSelector,
    private val descriptionPermuter: DescriptionPermuter,
    private val aggregationSelector: AggregationSelector,
    private val discretionaryTransactionSorter: DiscretionaryTransactionSorter,
    private val discretionaryTransactionsBeautifier: DiscretionaryTransactionsBeautifier,
) {
    fun execute(importInput: ImportInput): ImportOutput {
        // 1. Read the expected expenses using the classes in com.amarple.expense.read.expected
        val expectedExpenses = expectedExpenseReader.read(importInput.expectedExpenses)

        // 1.a. Read the description patterns, which will be used when matching expected expenses
        val expectedExpensePatterns = patternExpectedExpenseReader.read(importInput.expectedExpenses.descriptionPatterns)

        // 2. Read the configuration for categories, which will be used when categorizing expenses
        val allCategories = importInput.categories.hierarchy?.let { categoryHierarchyReader.read(it) }
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
        val expectedExpenseMatcher = DescriptionPatternExpectedExpenseMatcher(expectedExpensePatterns, descriptionPermuter)
        val matchedTransactionsByExpenseIndex = mutableMapOf<Int, MutableList<Transaction<*>>>() // Index of expected expense to transactions
        val transactionToMatchedExpectedIndexes = mutableMapOf<Transaction<*>, MutableList<Int>>()

        expectedExpenses.forEachIndexed { index, expectedExpense: ExpectedExpense<*> ->
            expectedExpenseMatcher.match(expectedExpense, allTransactions).forEach { transaction: Transaction<*> ->
                matchedTransactionsByExpenseIndex.getOrPut(index) { mutableListOf() }.add(transaction)
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
            DescriptionPatternCategorizer(categoryPatterns, descriptionPermuter),
            StaticCategorizer(Category("Entertainment", "Miscellaneous"))
        )
        // 5.c. TODO: find a way to categorize incoming deposits and outgoing transfers so that we can return them separately

        // 6. Calculate new AggregatedTransactions
        val aggregator: AggregateBy = aggregationSelector.getAggregator(importInput.aggregation.aggregationType)
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
        val (discretionarySpendTransactions, incomeAndTransferTransactions) = categorizedTransactions.partition {
            it.category != PAYMENTS  // 6.b.1. payments,
                && it.category != REWARDS // 6.b.2. rewards,
                && it.category != DEPOSITS // 6.b.3. and incoming deposits,
                && it.category != TRANSFERS // 6.b.4 and transfers between accounts
        }
        // TODO: add rewards and deposits as income
        // TODO: do we want to track or validate payments?
        // TODO: do we want to track transfers as income (like deposits)?
        // 6.c. ... and group transactions by category and instrument
        val aggregatedDiscretionarySpendTransactions = aggregator.aggregate(discretionarySpendTransactions)
            .let { discretionaryTransactionSorter.sort(it, importInput.aggregation.aggregationType) }
            .let {
                if (importInput.aggregation.aggregationType == AggregationType.Category && !allCategories.isNullOrEmpty()) {
                    discretionaryTransactionsBeautifier.beautifyForCategory(it, allCategories)
                } else {
                    it
                }
            }

        // 7. Build the response, including ...
        // 7.a. ... a list of transactions for expected expenses, in the same order as config, with null to designate expenses where no transaction was matched
        val expectedResults = expectedExpenses.mapIndexed { index, expense ->
            matchedTransactionsByExpenseIndex[index]
                ?.let { AggregatedTransaction(transactions = it, description = expense.name) }
                ?.updateCategory(category = expense.expectedTransaction.category)
        }

        // 7.e. ... warnings for ...
        val warnings = mutableListOf<String>()
        // 7.e.2. ... any expected expenses that matched multiple transactions
        matchedTransactionsByExpenseIndex.forEach { (index, transactions) ->
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
        // 7.e.4 ... any expected expenses that did not match any transactions
        expectedExpenses.forEachIndexed { index, expense ->
            if (!matchedTransactionsByExpenseIndex.containsKey(index)) {
                warnings.add("Expected expense '${expense.name}' did not match any transactions")
            }
        }

        incomeAndTransferTransactions.forEach { transaction ->
            warnings.add("Transaction '${transaction}' found but not included.")
        }

        return ImportOutput(
            expectedExpenses = expectedResults,
            categorizedExpenses = aggregatedDiscretionarySpendTransactions,
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