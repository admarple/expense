package com.amarple.expense.read.expected

import com.amarple.expense.model.ExpectedExpensesInput
import com.amarple.expense.model.internal.ExpectedExpense

interface ExpectedExpenseReader {
    fun read(input: ExpectedExpensesInput): List<ExpectedExpense<*>>
}