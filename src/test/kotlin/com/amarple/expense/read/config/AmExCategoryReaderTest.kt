package com.amarple.expense.read.config

import com.amarple.expense.model.AmExCategoryInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AmExCategoryReaderTest {

    @Test
    fun `should read AmEx categories from CSV`() {
        val reader = AmExCategoryReader()
        val csvPath = this::class.java.getResource("/amex_categories.csv")!!.path
        val result = reader.read(AmExCategoryInput(csvPath))

        assertEquals(13, result.size)
        assertEquals("Restaurant", result[0].amExCategory)
        assertEquals("Restaurant", result[0].amExSubcategory)
        assertEquals("Entertainment", result[0].category.category)
        assertEquals("Restaurants & Bars", result[0].category.subcategory)

        assertEquals("Restaurant", result[2].amExCategory)
        assertEquals(null, result[2].amExSubcategory)
        assertEquals("Entertainment", result[2].category.category)
        assertEquals("Restaurants & Bars", result[2].category.subcategory)

        assertEquals("Entertainment", result[12].amExCategory)
        assertEquals("Theatrical Events", result[12].amExSubcategory)
        assertEquals("Entertainment", result[12].category.category)
        assertEquals("Concerts & Events", result[12].category.subcategory)
    }
}
