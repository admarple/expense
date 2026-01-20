package com.amarple.expense.read.config

import com.amarple.expense.model.CategoryHierarchyInput
import com.amarple.expense.model.internal.Category
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CategoryHierarchyReaderTest {

    @Test
    fun `should read category hierarchy from CSV`() {
        val reader = CategoryHierarchyReader()
        val csvPath = this::class.java.getResource("/category_hierarchy.csv")!!.path
        val result = reader.read(CategoryHierarchyInput(csvPath))

        assertEquals(42, result.size)

        // Verify some specific entries
        assertTrue(result.any { it.category == "Housing" && it.subcategory == "Miscellaneous" })
        assertTrue(result.any { it.category == "Housing" && it.subcategory == "Tax" })
        assertTrue(result.any { it.category == "Health" && it.subcategory == "Habits, e.g. Gym" })
        assertTrue(result.any { it.category == "Financial_Services" && it.subcategory == "Deposits" })
        assertTrue(result.any { it.category == "Gifts" && it.subcategory == "Friends & Family" })

        // Check order for a few
        assertEquals(Category("Housing", "Miscellaneous"), result[0])
        assertEquals(Category("Housing", "Tax"), result[1])
        assertEquals(Category("Utilities", "Miscellaneous"), result[4])
    }
}
