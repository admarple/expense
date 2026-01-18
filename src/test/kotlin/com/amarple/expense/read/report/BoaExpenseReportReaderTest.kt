package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.ExpenseReportType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BoaExpenseReportReaderTest {
    @Test
    fun `should read Bank of America expenses from CSV`() {
        val csvPath = this::class.java.getResource("/boa_expenses.csv")!!.path
        val reader = BoaExpenseReportReader()
        val result = reader.read(ExpenseReportInput(csvPath, "Alex's Bank of America", reportType = ExpenseReportType.BankOfAmerica))

        assertEquals(7, result.transactions.size)

        val t1 = result.transactions.first()
        assertEquals(LocalDate.of(2025, 12, 12), t1.date)
        assertEquals("11111111111111111111111", t1.referenceNumber)
        assertEquals("Rally House Wayne PA", t1.description)
        assertEquals("Wayne         PA ", t1.address)
        assertEquals(20.0, t1.amount)
        assertEquals("Alex's Bank of America", t1.instrument?.name)

        val t2 = result.transactions.last()
        assertEquals(LocalDate.of(2025, 12, 6), t2.date)
        assertEquals("11111111111111111111117", t2.referenceNumber)
        assertEquals("SQ *FERMENTARIA Ardmore PA", t2.description)
        assertEquals("Ardmore       PA ", t2.address)
        assertEquals(-10.0, t2.amount)
        assertEquals("Alex's Bank of America", t2.instrument?.name)
    }
}
