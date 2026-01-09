package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AmExExpenseReportReaderTest {
    @Test
    fun `read should throw NotImplementedError`() {
        val reader = AmExExpenseReportReader()
        assertThrows<NotImplementedError> {
            reader.read(ExpenseReportInput("", "AmEx"))
        }
    }
}
