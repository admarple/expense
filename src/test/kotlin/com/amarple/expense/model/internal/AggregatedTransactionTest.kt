package com.amarple.expense.model.internal

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AggregatedTransactionTest {

    @Test
    fun `should calculate aggregate values correctly`() {
        val instrument = PaymentInstrument("Card")
        val category = Category("Food", "Misc")

        val t1 = BasicTransaction(LocalDate.of(2023, 1, 1), -10.0, "Desc 1", category, instrument)
        val t2 = BasicTransaction(LocalDate.of(2023, 1, 10), -20.0, "Desc 2", category, instrument)

        val agg = AggregatedTransaction(listOf(t1, t2))

        assertEquals(-30.0, agg.amount)
        assertEquals(LocalDate.of(2023, 1, 10), agg.date)
        assertEquals("aggregated transactions: 2", agg.description)
        assertEquals(instrument, agg.instrument)
        assertEquals(category, agg.category)
    }
}
