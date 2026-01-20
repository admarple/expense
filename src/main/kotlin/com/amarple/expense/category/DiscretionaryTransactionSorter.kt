package com.amarple.expense.category

import com.amarple.expense.model.AggregationType
import com.amarple.expense.model.internal.Transaction
import kotlin.collections.sortedWith

class DiscretionaryTransactionSorter {
    fun sort(transactions: List<Transaction<*>>, aggregationType: AggregationType?): List<Transaction<*>> {
        return transactions.sortedWith(
            when (aggregationType) {
                AggregationType.Instrument -> compareBy(
                    { transaction: Transaction<*> -> transaction.instrument?.name }
                )
                AggregationType.Category -> compareBy(
                    { transaction: Transaction<*> -> transaction.category?.category },
                    { transaction: Transaction<*> -> transaction.category?.subcategory }
                )
                else -> compareBy(
                    { transaction: Transaction<*> -> transaction.instrument?.name },
                    { transaction: Transaction<*> -> transaction.category?.category },
                    { transaction: Transaction<*> -> transaction.category?.subcategory }
                )
            }
        )
    }
}

