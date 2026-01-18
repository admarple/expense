package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.ExpenseReportType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class WellsFargoExpenseReportReaderTest {
    @Test
    fun `should read Bank of America expenses from CSV`() {
        val csvPath = this::class.java.getResource("/wellsfargo_expenses.csv")!!.path
        val reader = WellsFargoExpenseReportReader()
        val result = reader.read(ExpenseReportInput(csvPath, "Joint Wells Fargo", reportType = ExpenseReportType.WellsFargo))

        assertEquals(10, result.transactions.size)

        val t1 = result.transactions.first()
        assertEquals(LocalDate.of(2025, 12, 22), t1.date)
        assertEquals("VERIZON PAYMENTREC URRING 1234567890001 ALEX OLDSKOOL", t1.description)
        assertEquals(-39.99, t1.amount)
        assertEquals("Joint Wells Fargo", t1.instrument?.name)

        val t2 = result.transactions.last()
        assertEquals(LocalDate.of(2025, 12, 2), t2.date)
        assertEquals("OLD EMPLOYER COBRA COBRA 111111 COB11111111 ALEX OLDSKOOL", t2.description)
        assertEquals(-345.67, t2.amount)
        assertEquals("Joint Wells Fargo", t2.instrument?.name)
    }
}
