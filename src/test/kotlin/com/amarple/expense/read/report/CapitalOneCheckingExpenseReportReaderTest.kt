package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.ExpenseReportType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CapitalOneCheckingExpenseReportReaderTest {
    @Test
    fun `should read Capital One Checking expenses from CSV`() {
        val csvPath = this::class.java.getResource("/capitalone_checking_expenses.csv")!!.path
        val reader = CapitalOneCheckingExpenseReportReader()
        val result = reader.read(ExpenseReportInput(csvPath, "Holly's Capital One Checking", reportType = ExpenseReportType.CapitalOneChecking))

        assertEquals(3, result.transactions.size)

        val t1 = result.transactions.first()
        assertEquals(LocalDate.of(2026, 1, 16), t1.date)
        assertEquals("Withdrawal from ATT PAYMENT", t1.description)
        assertEquals(-34.56, t1.amount)
        assertEquals("Holly's Capital One Checking", t1.instrument?.name)

        val t2 = result.transactions.last()
        assertEquals(LocalDate.of(2026, 1, 13), t2.date)
        assertEquals("Deposit from WELLS FARGO IFI DDA TO DDA ZZZZZZZZZZ", t2.description)
        assertEquals(123.45, t2.amount)
        assertEquals("Holly's Capital One Checking", t2.instrument?.name)
    }
}
