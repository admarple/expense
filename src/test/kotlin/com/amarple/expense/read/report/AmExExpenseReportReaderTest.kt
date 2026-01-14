package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AmExExpenseReportReaderTest {
    @Test
    fun `should read Bank of America expenses from CSV`() {
        val csvPath = this::class.java.getResource("/amex_expenses.csv")!!.path
        val reader = AmExExpenseReportReader()
        val result = reader.read(ExpenseReportInput(csvPath, "Alex's AmEx", reportType = ExpenseReportType.AmericanExpress))

        assertEquals(3, result.transactions.size)

        val t1 = result.transactions.first()
        assertEquals(LocalDate.of(2025, 12, 3), t1.date)
        assertEquals("STATE FARM INSURANCEBLOOMINGTON         IL", t1.description)
        assertEquals(-123.45, t1.amount)
        assertEquals(
            """
            ZZZZZZZZ    0000000000
            STATE FARM INSURANCE
            BLOOMINGTON
            IL
            0000000000
            """.trimIndent(),
            t1.extendedDetails
        )
        assertEquals("STATE FARM INSURANCEBLOOMINGTON         IL", t1.appearsAs)
        assertEquals("1 STATE FARM PLZ", t1.address)
        assertEquals(
            """
            BLOOMINGTON
            IL
            """.trimIndent(),
            t1.cityState
        )
        assertEquals("61710-0001", t1.zipCode)
        assertEquals("UNITED STATES", t1.country)
        assertEquals("111111111111111111", t1.reference)
        assertEquals("Business Services-Insurance Services", t1.amExCategory)
        assertEquals("Alex's AmEx", t1.instrument?.name)

        val t2 = result.transactions.last()
        assertEquals(LocalDate.of(2025, 12, 16), t2.date)
        assertEquals("TST* LA CABRA BREWINBERWYN              PA", t2.description)
        assertEquals(-23.45, t2.amount)
        assertEquals(
            """
            99999999999 6102407908
            TST* LA CABRA BREWING 000000000
            BERWYN
            PA
            Description : BARS Price : 0.9019
            6102407908
            """.trimIndent(),
            t2.extendedDetails
        )
        assertEquals("TST* LA CABRA BREWINBERWYN              PA", t2.appearsAs)
        assertEquals("642 LANCASTER AVE ACROSS FROM THE BERWYN TRAIN STA", t2.address)
        assertEquals(
            """
            BERWYN
            PA
            """.trimIndent(),
            t2.cityState
        )
        assertEquals("19312-1663", t2.zipCode)
        assertEquals("UNITED STATES", t2.country)
        assertEquals("111111111111111112", t2.reference)
        assertEquals("Restaurant-Bar & Café", t2.amExCategory)
        assertEquals("Alex's AmEx", t2.instrument?.name)
    }
}
