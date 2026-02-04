package com.amarple.expense.read.config

import com.amarple.expense.model.DiscoverCategoryInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DiscoverCategoryReaderTest {

    @Test
    fun `should read Capital One categories from CSV`() {
        val reader = DiscoverCategoryReader()
        val csvPath = this::class.java.getResource("/discover_categories.csv")!!.path
        val result = reader.read(DiscoverCategoryInput(csvPath))

        assertEquals(9, result.size)

        result.first().let {
            assertEquals("Merchandise", it.discoverCategory)
            assertEquals("Shopping", it.category.category)
            assertEquals("Miscellaneous", it.category.subcategory)
        }

        result.last().let {
            assertEquals("Awards and Rebate Credits", it.discoverCategory)
            assertEquals("Financial_Services", it.category.category)
            assertEquals("Rewards", it.category.subcategory)
        }
    }
}
