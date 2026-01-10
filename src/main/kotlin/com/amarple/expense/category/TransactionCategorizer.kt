package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction

interface TransactionCategorizer {
    fun <T : Transaction<T>> categorize(transaction: T): Category?
}