package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.read.report.DiscoverCategory
import com.amarple.expense.read.report.DiscoverTransaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DiscoverCategoryCategorizerTest {

    @Test
    fun `should categorize Discover transactions correctly`() {
        val categorizer = DiscoverCategoryCategorizer()

        val transactions = listOf(
            createDiscoverTransaction(DiscoverCategory.MERCHANDISE),
            createDiscoverTransaction(DiscoverCategory.RESTAURANTS),
            createDiscoverTransaction(DiscoverCategory.SUPERMARKETS),
            createDiscoverTransaction(DiscoverCategory.SERVICES),
            createDiscoverTransaction(DiscoverCategory.PAYMENTS_AND_CREDITS),
            createDiscoverTransaction(DiscoverCategory.TRAVEL_AND_ENTERTAINMENT),
            createDiscoverTransaction(DiscoverCategory.GOVERNMENT_SERVICES),
            createDiscoverTransaction(DiscoverCategory.AWARDS_AND_REBATE_CREDITS),
            createDiscoverTransaction(DiscoverCategory.UNRECOGNIZED)
        )

        assertEquals(Category("Shopping", "Miscellaneous"), categorizer.categorize(transactions[0]))
        assertEquals(Category("Entertainment", "Restaurants & Bars"), categorizer.categorize(transactions[1]))
        assertEquals(Category("Grocery", "Miscellaneous"), categorizer.categorize(transactions[2]))
        assertEquals(Category("Utilities", "Miscellaneous"), categorizer.categorize(transactions[3]))
        assertEquals(Category("Financial_Services", "Payment & Credits"), categorizer.categorize(transactions[4]))
        assertEquals(Category("Travel", "Miscellaneous"), categorizer.categorize(transactions[5]))
        assertEquals(Category("Financial_Services", "Fines & Fees"), categorizer.categorize(transactions[6]))
        assertEquals(Category("Financial_Services", "Rewards"), categorizer.categorize(transactions[7]))
        assertEquals(Category("Entertainment", "Miscellaneous"), categorizer.categorize(transactions[8]))
    }

    private fun createDiscoverTransaction(category: DiscoverCategory): DiscoverTransaction {
        return DiscoverTransaction(
            transactionDate = LocalDate.now(),
            postDate = LocalDate.now(),
            amount = -10.0,
            description = "Some description",
            discoverCategoryName = category.displayName
        )
    }
}
