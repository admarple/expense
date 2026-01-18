package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.ExpenseReportType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CapitalOneExpenseReportReaderTest {
    @Test
    fun `should read Capital One expenses from CSV`() {
        val csvPath = this::class.java.getResource("/capitalone_expenses.csv")!!.path
        val reader = CapitalOneExpenseReportReader()
        val result = reader.read(ExpenseReportInput(csvPath, "Holly's Capital One Quicksilver", reportType = ExpenseReportType.CapitalOne))

        assertEquals(3, result.transactions.size)

        val t1 = result.transactions.first()
        assertEquals(LocalDate.of(2025, 12, 20), t1.date)
        assertEquals("CLASSPASS* MONTHLY", t1.description)
        assertEquals("Entertainment", t1.capitalOneCategory)
        assertEquals(-12.0, t1.amount)
        assertEquals("Holly's Capital One Quicksilver", t1.instrument?.name)

        val t2 = result.transactions.last()
        assertEquals(LocalDate.of(2025, 12, 1), t2.date)
        assertEquals("CAPITAL ONE AUTOPAY PYMT", t2.description)
        assertEquals("Payment/Credit", t2.capitalOneCategory)
        assertEquals(34.0, t2.amount)
        assertEquals("Holly's Capital One Quicksilver", t2.instrument?.name)
    }
}
