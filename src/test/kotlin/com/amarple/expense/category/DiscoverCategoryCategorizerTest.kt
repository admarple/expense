package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.read.report.DiscoverTransaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DiscoverCategoryCategorizerTest {
    lateinit var discoverCategoryList: List<DiscoverCategory>
    lateinit var categories: List<Category>
    lateinit var transactions: List<DiscoverTransaction>
    lateinit var categorizer: DiscoverCategoryCategorizer

    @BeforeEach
    fun setUp() {
        discoverCategoryList = listOf(
            DiscoverCategory("Merchandise", Category("Shopping", "Miscellaneous")),
            DiscoverCategory("Services", Category("Entertainment", "Miscellaneous")),
            DiscoverCategory("Payments and credits", Category("Utilities", "Miscellaneous")),
            DiscoverCategory("Travel/ Entertainment", Category("Financial_Services", "Payments")),
            DiscoverCategory("Restaurants", Category("Entertainment", "Restaurants & Bars")),
            DiscoverCategory("Supermarkets", Category("Grocery", "Miscellaneous")),
            DiscoverCategory("Government Services", Category("Financial_Services", "Fines & Fees")),
            DiscoverCategory("Awards and Rebate Credits", Category("Financial_Services", "Rewards")),
        )

        categorizer = DiscoverCategoryCategorizer(discoverCategoryList)
    }

    @Test
    fun `should categorize Discover transactions correctly`() {
        val transactions = listOf(
            createDiscoverTransaction("Merchandise"),
            createDiscoverTransaction("Services"),
            createDiscoverTransaction("Payments and credits"),
            createDiscoverTransaction("Travel/ Entertainment"),
            createDiscoverTransaction("Restaurants"),
            createDiscoverTransaction("Supermarkets"),
            createDiscoverTransaction("Government Services"),
            createDiscoverTransaction("Awards and Rebate Credits"),
            createDiscoverTransaction("Unrecognized"),
        )

        assertEquals(Category("Shopping", "Miscellaneous"), categorizer.categorize(transactions[0]))
        assertEquals(Category("Entertainment", "Miscellaneous"), categorizer.categorize(transactions[1]))
        assertEquals(Category("Utilities", "Miscellaneous"), categorizer.categorize(transactions[2]))
        assertEquals(Category("Financial_Services", "Payments"), categorizer.categorize(transactions[3]))
        assertEquals(Category("Entertainment", "Restaurants & Bars"), categorizer.categorize(transactions[4]))
        assertEquals(Category("Grocery", "Miscellaneous"), categorizer.categorize(transactions[5]))
        assertEquals(Category("Financial_Services", "Fines & Fees"), categorizer.categorize(transactions[6]))
        assertEquals(Category("Financial_Services", "Rewards"), categorizer.categorize(transactions[7]))
        assertEquals(null, categorizer.categorize(transactions[8]))
    }

    private fun createDiscoverTransaction(discoverCategory: String): DiscoverTransaction {
        return DiscoverTransaction(
            transactionDate = LocalDate.now(),
            postDate = LocalDate.now(),
            amount = -10.0,
            description = "Some description",
            discoverCategory = discoverCategory,
        )
    }
}
