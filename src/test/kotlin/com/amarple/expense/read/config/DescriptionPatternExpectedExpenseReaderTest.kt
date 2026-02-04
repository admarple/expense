package com.amarple.expense.read.config

import com.amarple.expense.model.DescriptionPatternExpectedExpensesInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DescriptionPatternExpectedExpenseReaderTest {

    @Test
    fun `should read description pattern expected expenses from CSV`() {
        val reader = DescriptionPatternExpectedExpenseReader()
        val csvPath = this::class.java.getResource("/expected_expenses_description_patterns.csv")!!.path
        val result = reader.read(DescriptionPatternExpectedExpensesInput(csvPath))

        assertEquals(6, result.size)

        result[0].let {
            assertEquals("^WHITETAIL DISPOSAL INC", it.pattern)
            assertEquals("WhiteTail (Waste)", it.expenseName)
            assertFalse { it.requirePriceMatch }
        }

        result[1].let {
            assertEquals("^GAS TEC", it.pattern)
            assertEquals("GasTec (Propane)", it.expenseName)
            assertFalse { it.requirePriceMatch }
        }

        result[5].let {
            assertEquals("^APPLE\\.COM/BILL", it.pattern)
            assertEquals("Apple Cloud Storage", it.expenseName)
            assertTrue { it.requirePriceMatch }
        }
    }
}
