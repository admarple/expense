package com.amarple.expense.model.internal

import java.time.LocalDate

data class ExpenseReport<T : Transaction>(
    val transactions: List<T>,
    val retrievalDate: LocalDate? = null
)
