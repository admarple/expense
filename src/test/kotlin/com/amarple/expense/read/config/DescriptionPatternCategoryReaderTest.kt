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
        assertEquals("^SEPTA.*CARD", result[0].pattern)
        assertEquals("Transportation", result[0].category.category)
        assertEquals("Public Transit", result[0].category.subcategory)

        assertEquals("^Rally House Wayne PA", result[1].pattern)
        assertEquals("Gifts", result[1].category.category)
        assertEquals("Friends & Family", result[1].category.subcategory)

        assertEquals("^SQ \\*", result[2].pattern)
        assertEquals("Entertainment", result[2].category.category)
        assertEquals("Restaurants & Bars", result[2].category.subcategory)
    }
}
