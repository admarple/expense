package com.amarple.expense.category

import com.amarple.expense.model.AggregationType
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

class AggregationSelector(
    private val aggregators: Map<AggregationType?, AggregateBy>
) {
    fun getAggregator(type: AggregationType?): AggregateBy = aggregators.getValue(type)
}

object AggregateNothing : AggregateBy {
    override fun groupingFun(t: Transaction<*>): Any? = t

    override fun aggregate(transactions: List<Transaction<*>>): List<Transaction<*>> = transactions
}

object AggregateEverything : AggregateBy { override fun groupingFun(t: Transaction<*>): Any? = null }

object AggregateByCategory : AggregateBy { override fun groupingFun(t: Transaction<*>): Any? = t.category }

object AggregateByInstrument : AggregateBy { override fun groupingFun(t: Transaction<*>): Any? = t.instrument }

object AggregateByCategoryAndInstrument : AggregateBy { override fun groupingFun(t: Transaction<*>): Any? = Pair(t.category, t.instrument) }