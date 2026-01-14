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

        assertEquals(5, result.size)
        assertEquals("^WHITETAIL DISPOSAL INC", result[0].pattern)
        assertEquals("WhiteTail (Waste)", result[0].expenseName)
        assertFalse { result[0].requirePriceMatch }

        assertEquals("^GAS TEC", result[1].pattern)
        assertEquals("GasTec (Propane)", result[1].expenseName)
        assertFalse { result[1].requirePriceMatch }

        assertEquals("^APPLE\\.COM/BILL", result[4].pattern)
        assertEquals("Apple Cloud Storage", result[4].expenseName)
        assertTrue { result[4].requirePriceMatch }
    }
}
