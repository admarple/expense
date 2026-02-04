package com.amarple.expense.read.config

import com.amarple.expense.model.DescriptionPatternCategoryInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DescriptionPatternCategoryReaderTest {

    @Test
    fun `should read description pattern categories from CSV`() {
        val reader = DescriptionPatternCategoryReader()
        val csvPath = this::class.java.getResource("/category_description_patterns.csv")!!.path
        val result = reader.read(DescriptionPatternCategoryInput(csvPath))

        assertEquals(6, result.size)

        result[0].let {
            assertEquals("^SEPTA.*CARD", it.pattern)
            assertEquals("Transportation", it.category.category)
            assertEquals("Public Transit", it.category.subcategory)
        }

        result[1].let {
            assertEquals("^Rally House Wayne PA", it.pattern)
            assertEquals("Gifts", it.category.category)
            assertEquals("Friends & Family", it.category.subcategory)
        }

        result[2].let {
            assertEquals("^SQ \\*", it.pattern)
            assertEquals("Entertainment", it.category.category)
            assertEquals("Restaurants & Bars", it.category.subcategory)
        }
    }
}
