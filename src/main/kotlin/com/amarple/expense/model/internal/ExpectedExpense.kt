package com.amarple.expense.model.internal

data class ExpectedExpense<T : Transaction<T>>(
    val name: String,
    val expectedTransaction: T,
)
