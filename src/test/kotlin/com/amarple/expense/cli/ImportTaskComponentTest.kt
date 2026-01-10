package com.amarple.expense.cli

import com.amarple.expense.model.ImportInput
import com.amarple.expense.model.ExpectedExpensesInput
import com.amarple.expense.model.DescriptionPatternExpectedExpensesInput
import com.amarple.expense.model.CategoriesInput
import com.amarple.expense.model.DescriptionPatternCategoryInput
import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.internal.Category
import com.amarple.expense.read.report.ExpenseReportType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertContains

class ImportTaskComponentTest {
    lateinit var discoverExpensesCsvPath: String
    lateinit var boaExpensesCsvPath: String
    lateinit var expectedExpensesCsvPath: String
    lateinit var expectedExpensesPatternsCsvPath: String
    lateinit var categoryPatternsCsvPath: String

    @BeforeEach
    fun setUp() {
        discoverExpensesCsvPath = this::class.java.getResource("/discover_expenses.csv")!!.path
        boaExpensesCsvPath = this::class.java.getResource("/boa_expenses.csv")!!.path
        expectedExpensesCsvPath = this::class.java.getResource("/expected_expenses.csv")!!.path
        expectedExpensesPatternsCsvPath = this::class.java.getResource("/expected_expenses_description_patterns.csv")!!.path
        categoryPatternsCsvPath = this::class.java.getResource("/category_description_patterns.csv")!!.path
    }

    @Test
    fun `execute should work with minimal input`() {
        val task = ImportTask()
        val input = ImportInput(
            reports = emptyList(),
            expectedExpenses = ExpectedExpensesInput(expectedExpensesCsvPath, DescriptionPatternExpectedExpensesInput(expectedExpensesPatternsCsvPath)),
            categories = CategoriesInput( DescriptionPatternCategoryInput(categoryPatternsCsvPath))
        )

        val result = task.execute(input)

        assertNotNull(result)
        assertEquals(6, result.expectedExpenses.size)
    }

    @Test
    fun `execute should work with non-empty reports`() {
        val task = ImportTask()
        val input = ImportInput(
            reports = listOf(
                ExpenseReportInput(
                    path = discoverExpensesCsvPath,
                    source = "Alex's Discover",
                    retrievalDate = LocalDate.now(),
                    reportType = ExpenseReportType.Discover,
                ),
                ExpenseReportInput(
                    path = boaExpensesCsvPath,
                    source = "Alex's Bank of America",
                    retrievalDate = LocalDate.now(),
                    reportType = ExpenseReportType.BankOfAmerica,
                ),
            ),
            expectedExpenses = ExpectedExpensesInput(expectedExpensesCsvPath, DescriptionPatternExpectedExpensesInput(expectedExpensesPatternsCsvPath)),
            categories = CategoriesInput( DescriptionPatternCategoryInput(categoryPatternsCsvPath))
        )

        val result = task.execute(input)

        assertNotNull(result)
        assertEquals(6, result.expectedExpenses.size)
        val matchedExpenses = result.expectedExpenses.filterNotNull()
        assertEquals(2, matchedExpenses.size)

        assertEquals("WhiteTail (Waste)", matchedExpenses[0].description)
        assertEquals(-13.0, result.expectedExpenses.filterNotNull()[0].amount)
        assertEquals("Alex's Discover", matchedExpenses[0].instrument?.name)
        assertEquals(Category("Utilities", "Miscellaneous"), matchedExpenses[0].category)

        assertEquals("GasTec (Propane)", matchedExpenses[1].description)
        assertEquals(-12.34, matchedExpenses[1].amount)
        assertEquals("Alex's Discover", matchedExpenses[1].instrument?.name)
        assertEquals(Category("Utilities", "Miscellaneous"), matchedExpenses[1].category)

        assertEquals(6, result.categorizedExpenses.size)
        assertContains(result.categorizedExpenses.map { it.category }, Category("Financial_Services", "Fines & Fees"))
        assertContains(result.categorizedExpenses.map { it.category }, Category("Transportation", "Public Transit"))
        assertContains(result.categorizedExpenses.map { it.category }, Category("Grocery", "Miscellaneous"))
        assertContains(result.categorizedExpenses.map { it.category }, Category("Gifts", "Friends & Family"))
        assertContains(result.categorizedExpenses.map { it.category }, Category("Entertainment", "Restaurants & Bars"))
        // Transactions not categorized will default to "Entertainment"/"Miscellaneous", per BespokeCategorizer
        assertContains(result.categorizedExpenses.map { it.category }, Category("Entertainment", "Miscellaneous"))
    }
}
