package com.amarple.expense.read.config

import com.amarple.expense.model.ChaseCategoryInput
import com.amarple.expense.model.internal.Category
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ChaseCategoryReaderTest {
    private val reader = ChaseCategoryReader()

    @Test
    fun `should read chase categories`() {
        val input = ChaseCategoryInput(path = "src/test/resources/chase_categories.csv")
        val categories = reader.read(input)

        assertEquals(9, categories.size)

        val first = categories[0]
        assertEquals("Bills & Utilities", first.chaseCategory)
        assertEquals(Category("Utilities", "Miscellaneous"), first.category)

        val second = categories[1]
        assertEquals("Entertainment", second.chaseCategory)
        assertEquals(Category("Entertainment", "Miscellaneous"), second.category)
    }
}
