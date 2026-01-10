package com.amarple.expense.cli

import com.amarple.expense.model.ImportInput
import com.amarple.expense.model.ExpectedExpensesInput
import com.amarple.expense.model.DescriptionPatternExpectedExpensesInput
import com.amarple.expense.model.CategoriesInput
import com.amarple.expense.model.DescriptionPatternCategoryInput
import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.read.report.ExpenseReportType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ImportTaskComponentTest {
    lateinit var discoverExpensesCsvPath: String
    lateinit var expectedExpensesCsvPath: String
    lateinit var expectedExpensesPatternsCsvPath: String
    lateinit var categoryPatternsCsvPath: String

    @BeforeEach
    fun setUp() {
        discoverExpensesCsvPath = this::class.java.getResource("/discover_expenses.csv")!!.path
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
                )
            ),
            expectedExpenses = ExpectedExpensesInput(expectedExpensesCsvPath, DescriptionPatternExpectedExpensesInput(expectedExpensesPatternsCsvPath)),
            categories = CategoriesInput( DescriptionPatternCategoryInput(categoryPatternsCsvPath))
        )

        val result = task.execute(input)

        assertNotNull(result)
        assertEquals(6, result.expectedExpenses.size)
    }
}
