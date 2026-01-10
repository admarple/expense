package com.amarple.expense.expected

import com.amarple.expense.model.internal.ExpectedExpense
import com.amarple.expense.model.internal.Transaction

interface ExpectedExpenseMatcher {
    fun <E : Transaction<E>, T : Transaction<T>> match(expectedExpense: ExpectedExpense<E>, transactions: List<T>): List<T> {
        return transactions.filter { isMatch(expectedExpense, it) }
    }

    fun <E : Transaction<E>, T : Transaction<T>> match(expectedExpenses: List<ExpectedExpense<E>>, transaction: T): List<ExpectedExpense<E>> {
        return expectedExpenses.filter { isMatch(it, transaction) }
    }

    fun <E : Transaction<E>, T : Transaction<T>> isMatch(expectedExpense: ExpectedExpense<E>, transaction: T): Boolean
}