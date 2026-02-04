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

        result.first().let {
            assertEquals("Airfare", it.capitalOneCategory)
            assertEquals("Travel", it.category.category)
            assertEquals("Miscellaneous", it.category.subcategory)
        }

        result.last().let {
            assertEquals("Payment/Credit", it.capitalOneCategory)
            assertEquals("Financial_Services", it.category.category)
            assertEquals("Payments", it.category.subcategory)
        }
    }
}
