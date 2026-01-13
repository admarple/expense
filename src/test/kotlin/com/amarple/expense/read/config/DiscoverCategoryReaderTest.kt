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

        assertEquals(8, result.size)
        assertEquals("Merchandise", result.first().discoverCategory)
        assertEquals("Shopping", result.first().category.category)
        assertEquals("Miscellaneous", result.first().category.subcategory)

        assertEquals("Awards and Rebate Credits", result.last().discoverCategory)
        assertEquals("Financial_Services", result.last().category.category)
        assertEquals("Rewards", result.last().category.subcategory)
    }
}
