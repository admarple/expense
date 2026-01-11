package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.read.report.AmExTransaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AmExCategoryCategorizerTest {
    lateinit var amExCategoryList: List<AmExCategory>
    lateinit var categories: List<Category>
    lateinit var transactions: List<AmExTransaction>
    lateinit var categorizer: AmExCategoryCategorizer

    @BeforeEach
    fun setUp() {
        amExCategoryList = listOf(
            AmExCategory("Transportation", "Taxis & Coach", Category("Transportation", "Rideshare")),
            AmExCategory("Transportation", "Fuel", Category("Transportation", "Car")),
            AmExCategory("Transportation", null, Category("Transportation", "Miscellaneous")),
            AmExCategory("Business Services", "Insurance Services", Category("Financial_Services", "Insurance")),
            AmExCategory("Business Services", "Health Care Services", Category("Health", "Physical Healthcare")),
        )

        categories = listOf(
            Category("Transportation", "Car"),
            Category("Transportation", "Miscellaneous"),
            Category("Financial_Services", "Insurance"),
        )

        transactions = listOf(
            AmExTransaction(date = LocalDate.now(), amount = -10.0, description = "gas station", amExCategory = "Transportation-Fuel"),
            AmExTransaction(date = LocalDate.now(), amount = -20.0, description = "EZ*Pass", amExCategory = "Transportation-Toll"),
            AmExTransaction(date = LocalDate.now(), amount = -30.0, description = "jewelry insurance", amExCategory = "Business Services-Insurance Services"),
            AmExTransaction(date = LocalDate.now(), amount = -40.0, description = "dinner out", amExCategory = "Restaurant-Restaurant"),
        )

        categorizer = AmExCategoryCategorizer(amExCategoryList)
    }

    @Test
    fun `should categorize transaction based on category config`() {
        assertEquals(categories[0], categorizer.categorize(transactions[0]))
        assertEquals(categories[2], categorizer.categorize(transactions[2]))
    }

    @Test
    fun `should use category+null if no category+subcategory match exists`() {
        assertEquals(categories[1], categorizer.categorize(transactions[1]))
    }

    @Test
    fun `should return null if no category+null match exists`() {
        assertNull(categorizer.categorize(transactions[3]))
    }
}
