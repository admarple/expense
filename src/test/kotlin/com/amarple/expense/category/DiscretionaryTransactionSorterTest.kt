package com.amarple.expense.category

import com.amarple.expense.model.AggregationType
import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.PaymentInstrument
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DiscretionaryTransactionSorterTest {
    private val sorter = DiscretionaryTransactionSorter()

    private val instrumentA = PaymentInstrument("A")
    private val instrumentB = PaymentInstrument("B")

    private val category1 = Category("C1", "S1")
    private val category2 = Category("C1", "S2")
    private val category3 = Category("C2", "S1")

    private val t1 = BasicTransaction(LocalDate.now(), -10.0, "t1", category3, instrumentB)
    private val t2 = BasicTransaction(LocalDate.now(), -20.0, "t2", category1, instrumentA)
    private val t3 = BasicTransaction(LocalDate.now(), -30.0, "t3", category2, instrumentA)
    private val t4 = BasicTransaction(LocalDate.now(), -40.0, "t4", category1, instrumentB)

    @Test
    fun `should sort by instrument when aggregation type is Instrument`() {
        val transactions = listOf(t1, t2, t3, t4)
        val sorted = sorter.sort(transactions, AggregationType.Instrument)

        // instrumentA: t2, t3
        // instrumentB: t1, t4
        // Original order for same instrument is preserved if stable, but here we just check instrument name
        assertEquals(listOf(t2, t3, t1, t4), sorted)
    }

    @Test
    fun `should sort by category and subcategory when aggregation type is Category`() {
        val transactions = listOf(t1, t2, t3, t4)
        val sorted = sorter.sort(transactions, AggregationType.Category)

        // category1 (C1, S1): t2, t4
        // category2 (C1, S2): t3
        // category3 (C2, S1): t1
        assertEquals(listOf(t2, t4, t3, t1), sorted)
    }

    @Test
    fun `should sort by instrument, then category, then subcategory by default`() {
        val transactions = listOf(t1, t2, t3, t4)

        // AggregationType.CategoryAndInstrument or null triggers else branch
        val sorted = sorter.sort(transactions, AggregationType.CategoryAndInstrument)

        // instrumentA:
        //   category1 (C1, S1): t2
        //   category2 (C1, S2): t3
        // instrumentB:
        //   category1 (C1, S1): t4
        //   category3 (C2, S1): t1
        assertEquals(listOf(t2, t3, t4, t1), sorted)
    }

    @Test
    fun `should handle null aggregation type by using default sorting`() {
        val transactions = listOf(t1, t2, t3, t4)
        val sorted = sorter.sort(transactions, null)

        assertEquals(listOf(t2, t3, t4, t1), sorted)
    }

    @Test
    fun `should handle null instrument or category during sorting`() {
        val tNullInstrument = BasicTransaction(LocalDate.now(), -50.0, "tNullInstrument", category1, null)
        val tNullCategory = BasicTransaction(LocalDate.now(), -60.0, "tNullCategory", null, instrumentA)

        val transactions = listOf(t1, tNullInstrument, tNullCategory)

        // Sorting by Instrument: nulls first by default in compareBy?
        // actually compareBy { it.instrument?.name } will result in null for tNullInstrument
        // Kotlin's compareBy with null values: nulls are considered less than non-nulls by default?
        // Let's verify what happens.
        val sorted = sorter.sort(transactions, AggregationType.Instrument)

        // tNullInstrument: instrument = null -> name = null
        // tNullCategory: instrument = instrumentA -> name = "A"
        // t1: instrument = instrumentB -> name = "B"
        assertEquals(listOf(tNullInstrument, tNullCategory, t1), sorted)
    }
}
