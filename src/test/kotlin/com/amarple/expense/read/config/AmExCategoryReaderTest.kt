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

        assertEquals(14, result.size)

        result[0].let {
            assertEquals("Restaurant", it.amExCategory)
            assertEquals("Restaurant", it.amExSubcategory)
            assertEquals("Entertainment", it.category.category)
            assertEquals("Restaurants & Bars", it.category.subcategory)
        }

        result[2].let {
            assertEquals("Restaurant", it.amExCategory)
            assertEquals(null, it.amExSubcategory)
            assertEquals("Entertainment", it.category.category)
            assertEquals("Restaurants & Bars", it.category.subcategory)
        }

        result[12].let {
            assertEquals("Entertainment", it.amExCategory)
            assertEquals("Theatrical Events", it.amExSubcategory)
            assertEquals("Entertainment", it.category.category)
            assertEquals("Concerts & Events", it.category.subcategory)
        }
    }
}
