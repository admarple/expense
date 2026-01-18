package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.ExpenseReportType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ChaseExpenseReportReaderTest {
    private val reader = ChaseExpenseReportReader()

    @Test
    fun `should read chase expenses`() {
        val input = ExpenseReportInput(
            path = "src/test/resources/chase_expenses.csv",
            source = "Chase",
            reportType = ExpenseReportType.Chase
        )

        val report = reader.read(input)

        assertEquals(2, report.transactions.size)

        val first = report.transactions[0]
        assertEquals(LocalDate.of(2025, 12, 23), first.date)
        assertEquals("NATGEO MAG 8006475463", first.description)
        assertEquals("Bills & Utilities", first.chaseCategory)
        assertEquals(-79.0, first.amount)
        assertEquals("Chase", first.instrument?.name)

        val second = report.transactions[1]
        assertEquals(LocalDate.of(2025, 12, 2), second.date)
        assertEquals("HELP.HBOMAX.COM", second.description)
        assertEquals("Entertainment", second.chaseCategory)
        assertEquals(-19.6, second.amount)
        assertEquals("Chase", second.instrument?.name)
    }
}
