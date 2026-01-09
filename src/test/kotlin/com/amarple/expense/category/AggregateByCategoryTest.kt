package com.amarple.expense.category

import com.amarple.expense.model.internal.AggregatedTransaction
import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.Category
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AggregateByCategoryTest {

    @Test
    fun `should aggregate transactions by category`() {
        val category1 = Category("Shopping", "Misc")
        val category2 = Category("Food", "Grocery")

        val t1 = BasicTransaction(LocalDate.of(2023, 1, 1), -10.0, "store 1", category1, null)
        val t2 = BasicTransaction(LocalDate.of(2023, 1, 2), -20.0, "store 2", category1, null)
        val t3 = BasicTransaction(LocalDate.of(2023, 1, 3), -40.0, "grocery 1", category2, null)

        val aggregator = AggregateByCategory()
        val result = aggregator.aggregate(listOf(t1, t2, t3))

        assertEquals(2, result.size)

        val agg1 = result.find { it.category == category1 } as AggregatedTransaction
        assertEquals(-30.0, agg1.amount)
        assertEquals(2, agg1.transactions.size)

        val agg2 = result.find { it.category == category2 } as AggregatedTransaction
        assertEquals(-40.0, agg2.amount)
        assertEquals(1, agg2.transactions.size)
    }

    @Test
    fun `should handle null categories`() {
        val t1 = BasicTransaction(LocalDate.of(2023, 1, 1), -10.0, "store 1", null, null)
        val t2 = BasicTransaction(LocalDate.of(2023, 1, 2), -20.0, "store 2", null, null)

        val aggregator = AggregateByCategory()
        val result = aggregator.aggregate(listOf(t1, t2))

        assertEquals(1, result.size)
        val agg = result[0] as AggregatedTransaction
        assertEquals(null, agg.category)
        assertEquals(-30.0, agg.amount)
    }
}
