package com.amarple.expense.category

import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.Category
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class StaticCategorizerTest {

    @Test
    fun `should categorize transaction to default value`() {
        val category1 = Category("Shopping", "Misc")
        val category2 = Category("Food", "Grocery")

        val t1 = BasicTransaction(LocalDate.now(), -10.0, "AMAZON.COM*123", null, null)
        val t2 = BasicTransaction(LocalDate.now(), -20.0, "SAFEWAY STORE 456", null, null)
        val t3 = BasicTransaction(LocalDate.now(), -30.0, "UNKNOWN STORE", null, null)

        val categorizer1 = StaticCategorizer(category1)
        assertEquals(category1, categorizer1.categorize(t1))
        assertEquals(category1, categorizer1.categorize(t2))
        assertEquals(category1, categorizer1.categorize(t3))

        val categorizer2 = StaticCategorizer(category2)
        assertEquals(category2, categorizer2.categorize(t1))
        assertEquals(category2, categorizer2.categorize(t2))
        assertEquals(category2, categorizer2.categorize(t3))
    }
}
