package com.amarple.expense.read.config

import com.amarple.expense.model.DescriptionPatternExpectedExpensesInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DescriptionPatternExpectedExpenseReaderTest {

    @Test
    fun `should read description pattern expected expenses from CSV`() {
        val reader = DescriptionPatternExpectedExpenseReader()
        val csvPath = this::class.java.getResource("/expected_expenses_description_patterns.csv")!!.path
        val result = reader.read(DescriptionPatternExpectedExpensesInput(csvPath))

        assertEquals(4, result.size)
        assertEquals("^WHITETAIL DISPOSAL INC", result[0].pattern)
        assertEquals("WhiteTail (Waste)", result[0].expenseName)

        assertEquals("^GAS TEC", result[1].pattern)
        assertEquals("GasTec (Propane)", result[1].expenseName)
    }
}
