package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CapitalOneExpenseReportReaderTest {
    @Test
    fun `read should throw NotImplementedError`() {
        val reader = CapitalOneExpenseReportReader()
        assertThrows<NotImplementedError> {
            reader.read(ExpenseReportInput("", "CapitalOne", reportType = ExpenseReportType.CapitalOne))
        }
    }
}
