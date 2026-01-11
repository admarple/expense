package com.amarple.expense.cli

import com.amarple.expense.model.AmExCategoryInput
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
import kotlin.test.assertTrue

class ImportTaskComponentTest {
    lateinit var discoverExpensesCsvPath: String
    lateinit var boaExpensesCsvPath: String
    lateinit var wellsFargoExpensesCsvPath: String
    lateinit var amExExpensesCsvPath: String
    lateinit var expectedExpensesCsvPath: String
    lateinit var expectedExpensesPatternsCsvPath: String
    lateinit var categoryPatternsCsvPath: String
    lateinit var amExCategoriesCsvPath: String

    @BeforeEach
    fun setUp() {
        discoverExpensesCsvPath = this::class.java.getResource("/discover_expenses.csv")!!.path
        boaExpensesCsvPath = this::class.java.getResource("/boa_expenses.csv")!!.path
        wellsFargoExpensesCsvPath = this::class.java.getResource("/wellsfargo_expenses.csv")!!.path
        amExExpensesCsvPath = this::class.java.getResource("/amex_expenses.csv")!!.path
        expectedExpensesCsvPath = this::class.java.getResource("/expected_expenses.csv")!!.path
        expectedExpensesPatternsCsvPath = this::class.java.getResource("/expected_expenses_description_patterns.csv")!!.path
        categoryPatternsCsvPath = this::class.java.getResource("/category_description_patterns.csv")!!.path
        amExCategoriesCsvPath = this::class.java.getResource("/amex_categories.csv")!!.path
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
                ExpenseReportInput(
                    path = wellsFargoExpensesCsvPath,
                    source = "Joint Wells Fargo",
                    retrievalDate = LocalDate.now(),
                    reportType = ExpenseReportType.WellsFargo,
                ),
                ExpenseReportInput(
                    path = amExExpensesCsvPath,
                    source = "Alex's AmEx",
                    retrievalDate = LocalDate.now(),
                    reportType = ExpenseReportType.AmericanExpress,
                ),
            ),
            expectedExpenses = ExpectedExpensesInput(expectedExpensesCsvPath, DescriptionPatternExpectedExpensesInput(expectedExpensesPatternsCsvPath)),
            categories = CategoriesInput(
                descriptionPatterns = DescriptionPatternCategoryInput(categoryPatternsCsvPath),
                amExCategories = AmExCategoryInput(amExCategoriesCsvPath)
            )
        )

        val result = task.execute(input)

        assertNotNull(result)
        assertEquals(6, result.expectedExpenses.size)
        val matchedExpenses = result.expectedExpenses.filterNotNull()
        assertEquals(4, matchedExpenses.size)

        assertEquals("WhiteTail (Waste)", matchedExpenses[0].description)
        assertEquals(-13.0, result.expectedExpenses.filterNotNull()[0].amount)
        assertEquals("Alex's Discover", matchedExpenses[0].instrument?.name)
        assertEquals(Category("Utilities", "Miscellaneous"), matchedExpenses[0].category)

        assertEquals("GasTec (Propane)", matchedExpenses[1].description)
        assertEquals(-12.34, matchedExpenses[1].amount)
        assertEquals("Alex's Discover", matchedExpenses[1].instrument?.name)
        assertEquals(Category("Utilities", "Miscellaneous"), matchedExpenses[1].category)

        assertEquals("COBRA", matchedExpenses[2].description)
        assertEquals(-345.67, matchedExpenses[2].amount)
        assertEquals("Joint Wells Fargo", matchedExpenses[2].instrument?.name)
        assertEquals(Category("Health", "Physical Healthcare"), matchedExpenses[2].category)

        assertEquals("Verizon (Internet)", matchedExpenses[3].description)
        assertEquals(-39.99, matchedExpenses[3].amount)
        assertEquals("Joint Wells Fargo", matchedExpenses[3].instrument?.name)
        assertEquals(Category("Utilities", "Miscellaneous"), matchedExpenses[3].category)

        assertEquals(9, result.categorizedExpenses.size)
        assertTrue { result.categorizedExpenses.any { it.category == Category("Financial_Services", "Fines & Fees") } }
        assertTrue { result.categorizedExpenses.any { it.category == Category("Transportation", "Public Transit") } }
        assertTrue { result.categorizedExpenses.any { it.category == Category("Grocery", "Miscellaneous") } }
        assertTrue { result.categorizedExpenses.any { it.category == Category("Gifts", "Friends & Family") } }
        assertTrue {
            result.categorizedExpenses.any {
                it.category == Category("Entertainment", "Restaurants & Bars")
                    && it.instrument?.name == "Alex's Bank of America"
            }
        }
        assertTrue {
            result.categorizedExpenses.any {
                it.category == Category("Financial_Services", "Insurance")
                    && it.instrument?.name == "Alex's AmEx"
            }
        }
        assertTrue {
            result.categorizedExpenses.any {
                it.category == Category("Entertainment", "Restaurants & Bars")
                    && it.instrument?.name == "Alex's AmEx"
            }
        }
        // Transactions not categorized will default to "Entertainment"/"Miscellaneous", per BespokeCategorizer
        // Note that discover_expenses.csv and amex_expenses.csv do not contain any unmatched transactions, so there is no "Entertainment"/"Miscellaneous" for the Alex's Discover or Alex's AmEx
        assertTrue {
            result.categorizedExpenses.any {
                it.category == Category("Entertainment", "Miscellaneous")
                    && it.instrument?.name == "Alex's Bank of America"
            }
        }
        assertTrue {
            result.categorizedExpenses.any {
                it.category == Category("Entertainment", "Miscellaneous")
                    && it.instrument?.name == "Joint Wells Fargo"
            }
        }

    }
}
