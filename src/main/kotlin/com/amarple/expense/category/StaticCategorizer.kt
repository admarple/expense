package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction

class StaticCategorizer(
    private val category: Category
): TransactionCategorizer {
    override fun <T : Transaction<*>> categorize(transaction: T): Category {
        return category
    }
}