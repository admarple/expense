package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.ExpenseReportType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DiscoverExpenseReportReaderTest {

    @Test
    fun `should read Discover expenses from CSV`() {
        val csvPath = this::class.java.getResource("/discover_expenses.csv")!!.path
        val reader = DiscoverExpenseReportReader()
        val result = reader.read(ExpenseReportInput(csvPath, "Alex's Discover", reportType = ExpenseReportType.Discover))

        assertEquals(7, result.transactions.size)

        val t1 = result.transactions.first()
        assertEquals(LocalDate.of(2025, 12, 1), t1.date)
        assertEquals(-13.0, t1.amount) // Discover negates amount
        assertEquals("WHITETAIL DISPOSAL INC 610-7540103 PA", t1.description)
        assertEquals("Services", t1.discoverCategory)
        assertEquals("Alex's Discover", t1.instrument?.name)

        val t2 = result.transactions.last()
        assertEquals(LocalDate.of(2025, 6, 29), t2.date)
        assertEquals(-34.56, t2.amount) // Discover negates amount
        assertEquals("WEGMANS # 46 MALVERN PA", t2.description)
        assertEquals("Supermarkets", t2.discoverCategory)
        assertEquals("Alex's Discover", t2.instrument?.name)
    }
}
