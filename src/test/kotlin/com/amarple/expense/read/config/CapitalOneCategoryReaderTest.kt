package com.amarple.expense.read.config

import com.amarple.expense.model.CapitalOneCategoryInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CapitalOneCategoryReaderTest {

    @Test
    fun `should read Capital One categories from CSV`() {
        val reader = CapitalOneCategoryReader()
        val csvPath = this::class.java.getResource("/capitalone_categories.csv")!!.path
        val result = reader.read(CapitalOneCategoryInput(csvPath))

        assertEquals(11, result.size)
        assertEquals("Airfare", result.first().capitalOneCategory)
        assertEquals("Travel", result.first().category.category)
        assertEquals("Miscellaneous", result.first().category.subcategory)

        assertEquals("Payment/Credit", result.last().capitalOneCategory)
        assertEquals("Financial_Services", result.last().category.category)
        assertEquals("Payment & Credits", result.last().category.subcategory)
    }
}
