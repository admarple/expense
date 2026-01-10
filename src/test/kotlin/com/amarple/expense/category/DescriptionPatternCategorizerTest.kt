package com.amarple.expense.category

import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.Category
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DescriptionPatternCategorizerTest {

    @Test
    fun `should categorize transaction based on description pattern`() {
        val category1 = Category("Shopping", "Misc")
        val category2 = Category("Food", "Grocery")

        val patterns = listOf(
            DescriptionPatternCategory(".*AMAZON.*", category1),
            DescriptionPatternCategory(".*SAFEWAY.*", category2)
        )

        val t1 = BasicTransaction(LocalDate.now(), -10.0, "AMAZON.COM*123", null, null)
        val t2 = BasicTransaction(LocalDate.now(), -20.0, "SAFEWAY STORE 456", null, null)
        val t3 = BasicTransaction(LocalDate.now(), -30.0, "UNKNOWN STORE", null, null)

        val categorizer = DescriptionPatternCategorizer(patterns)

        assertEquals(category1, categorizer.categorize(t1))
        assertEquals(category2, categorizer.categorize(t2))
        assertNull(categorizer.categorize(t3))
    }

    @Test
    fun `should use the first matching pattern`() {
        val category1 = Category("Cat1", "Sub1")
        val category2 = Category("Cat2", "Sub2")

        val patterns = listOf(
            DescriptionPatternCategory(".*MATCH.*", category1),
            DescriptionPatternCategory(".*", category2)
        )

        val t = BasicTransaction(LocalDate.now(), -10.0, "THIS IS A MATCH", null, null)

        val categorizer = DescriptionPatternCategorizer(patterns)

        assertEquals(category1, categorizer.categorize(t))
    }
}
