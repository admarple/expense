package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction

/**
 * This class tries to apply [TransactionCategorizer]s in order, using the first one that matches.
 */
class SequentialCategorizer(
    private val categorizers: List<TransactionCategorizer>,
): TransactionCategorizer {
    override fun <T : Transaction<*>> categorize(transaction: T): Category? {
        return categorizers.firstNotNullOf { it.categorize(transaction) }
    }
}