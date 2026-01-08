package com.amarple.expense.category

import com.amarple.expense.model.internal.AggregatedTransaction
import com.amarple.expense.model.internal.Transaction

class AggregateByCategory {
    fun aggregate(transactions: List<Transaction>): List<Transaction> {
        return transactions
            .groupBy { it.category }
            .map { AggregatedTransaction(it.value) }
    }
}