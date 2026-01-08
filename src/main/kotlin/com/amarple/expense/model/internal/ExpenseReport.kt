package com.amarple.expense.model.internal

import java.time.LocalDate

data class ExpenseReport(
    val transactions: List<Transaction>,
    val retrievalDate: LocalDate? = null
)

