package com.amarple.expense.category

import com.amarple.expense.model.internal.AggregatedTransaction
import com.amarple.expense.model.internal.Transaction

interface AggregateBy {
    fun groupingFun(t: Transaction<*>): Any?

    fun aggregate(transactions: List<Transaction<*>>): List<Transaction<*>> {
        return transactions
            .groupBy { groupingFun(it) }
            .map { AggregatedTransaction(it.value) }
    }
}

object AggregateByCategory : AggregateBy { override fun groupingFun(t: Transaction<*>): Any? = t.category }

object AggregateByInstrument : AggregateBy { override fun groupingFun(t: Transaction<*>): Any? = t.instrument }

object AggregateByCategoryAndInstrument : AggregateBy { override fun groupingFun(t: Transaction<*>): Any? = Pair(t.category, t.instrument) }