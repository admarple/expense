package com.amarple.expense.expected

import com.amarple.expense.model.internal.ExpectedExpense
import com.amarple.expense.model.internal.Transaction

interface ExpectedExpenseMatcher<T : Transaction> {
    fun match(expectedExpense: ExpectedExpense, transactions: List<T>): List<T> {
        return transactions.filter { isMatch(expectedExpense, it) }
    }

    fun match(expectedExpenses: List<ExpectedExpense>, transaction: T): List<ExpectedExpense> {
        return expectedExpenses.filter { isMatch(it, transaction) }
    }

    fun isMatch(expectedExpense: ExpectedExpense, transaction: T): Boolean
}