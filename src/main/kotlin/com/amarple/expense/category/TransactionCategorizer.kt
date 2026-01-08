package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction

interface TransactionCategorizer<T : Transaction> {
    fun categorize(transaction: T): Category?
}