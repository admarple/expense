package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BoaExpenseReportReaderTest {
    @Test
    fun `read should throw NotImplementedError`() {
        val reader = BoaExpenseReportReader()
        assertThrows<NotImplementedError> {
            reader.read(ExpenseReportInput("", "Boa", reportType = ExpenseReportType.BankOfAmerica))
        }
    }
}
