package com.amarple.expense.category

import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.Category
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DiscretionaryTransactionsBeautifierTest {

    @Test
    fun `beautifyForCategory should order transactions by category and add stubs for missing categories`() {
        val beautifier = DiscretionaryTransactionsBeautifier()

        val category1 = Category("Entertainment", "Media")
        val category2 = Category("Grocery", "Basics")
        val category3 = Category("Travel", "Vacation")
        val allCategories = listOf(category1, category2, category3)

        val tx1 = BasicTransaction(
            date = LocalDate.of(2026, 1, 1),
            amount = -10.0,
            description = "Entertainment - Media",
            category = category1,
            instrument = null
        )
        val tx2 = BasicTransaction(
            date = LocalDate.of(2026, 1, 3),
            amount = -15.0,
            description = "Travel - Vacation",
            category = category3,
            instrument = null
        )

        // Mixed input order
        val transactions = listOf(tx2, tx1)

        val result = beautifier.beautifyForCategory(transactions, allCategories)

        assertEquals(3, result.size)

        assertEquals(category1, result[0].category)
        assertEquals("Entertainment - Media", result[0].description)

        assertEquals(category2, result[1].category)
        assertEquals("No transactions found", result[1].description)
        assertEquals(0.0, result[1].amount)

        assertEquals(category3, result[2].category)
        assertEquals("Travel - Vacation", result[2].description)

    }
}
