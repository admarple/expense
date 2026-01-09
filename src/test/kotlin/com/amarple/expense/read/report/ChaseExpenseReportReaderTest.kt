package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChaseExpenseReportReaderTest {
    @Test
    fun `read should throw NotImplementedError`() {
        val reader = ChaseExpenseReportReader()
        assertThrows<NotImplementedError> {
            reader.read(ExpenseReportInput("", "Chase"))
        }
    }
}
