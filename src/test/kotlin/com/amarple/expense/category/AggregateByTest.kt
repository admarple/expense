package com.amarple.expense.category

import com.amarple.expense.model.internal.AggregatedTransaction
import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.PaymentInstrument
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AggregateByTest {
    lateinit var category1: Category
    lateinit var category2: Category
    lateinit var instrument1: PaymentInstrument
    lateinit var instrument2: PaymentInstrument
    lateinit var transactions: List<BasicTransaction>

    @BeforeEach
    fun setUp() {
        category1 = Category("Shopping", "Misc")
        category2 = Category("Food", "Grocery")
        instrument1 = PaymentInstrument("Card")
        instrument2 = PaymentInstrument("Cash")

        transactions = listOf(
            BasicTransaction(LocalDate.of(2023, 1, 1), -10.0, "store 1", category1, instrument1),
            BasicTransaction(LocalDate.of(2023, 1, 2), -20.0, "store 2", category1, instrument2),
            BasicTransaction(LocalDate.of(2023, 1, 3), -40.0, "grocery 1", category2, instrument2),
        )
    }

    @Test
    fun `should aggregate transactions by category`() {
        val aggregator = AggregateByCategory

        val result = aggregator.aggregate(transactions)

        assertEquals(2, result.size)

        val agg1 = result.find { it.category == category1 } as AggregatedTransaction
        assertEquals(-30.0, agg1.amount)
        assertEquals(2, agg1.transactions.size)

        val agg2 = result.find { it.category == category2 } as AggregatedTransaction
        assertEquals(-40.0, agg2.amount)
        assertEquals(1, agg2.transactions.size)
    }

    @Test
    fun `should aggregate transactions by instrument`() {
        val aggregator = AggregateByInstrument

        val result = aggregator.aggregate(transactions)

        assertEquals(2, result.size)

        val agg1 = result.find { it.instrument == instrument1 } as AggregatedTransaction
        assertEquals(-10.0, agg1.amount)
        assertEquals(1, agg1.transactions.size)

        val agg2 = result.find { it.instrument == instrument2 } as AggregatedTransaction
        assertEquals(-60.0, agg2.amount)
        assertEquals(2, agg2.transactions.size)
    }

    @Test
    fun `should aggregate transactions by category and instrument`() {
        val aggregator = AggregateByCategoryAndInstrument

        val result = aggregator.aggregate(transactions)

        assertEquals(3, result.size)

        transactions.forEachIndexed { index, transaction ->
            val agg = result[index] as AggregatedTransaction
            assertEquals(transaction.amount, agg.amount)
            assertEquals(1, agg.transactions.size)
            assertEquals(transaction.category, agg.category)
            assertEquals(transaction.instrument, agg.instrument)
        }
    }

    @Test
    fun `should handle null categories`() {
        val transactionWithNullCategory = transactions.map { it.copy(category = null) }

        val aggregator = AggregateByCategory

        val result = aggregator.aggregate(transactionWithNullCategory)

        assertEquals(1, result.size)
        val agg = result[0] as AggregatedTransaction
        assertEquals(null, agg.category)
        assertEquals(-70.0, agg.amount)
    }

    @Test
    fun `should handle null instruments`() {
        val transactionWithNullInstrument = transactions.map { it.copy(instrument = null) }

        val aggregator = AggregateByInstrument

        val result = aggregator.aggregate(transactionWithNullInstrument)

        assertEquals(1, result.size)
        val agg = result[0] as AggregatedTransaction
        assertEquals(null, agg.instrument)
        assertEquals(-70.0, agg.amount)
    }
}
