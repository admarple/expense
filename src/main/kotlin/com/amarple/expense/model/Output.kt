package com.amarple.expense.model

import com.amarple.expense.model.internal.Transaction
import java.time.YearMonth

data class ImportOutput(
    val expectedExpenses: List<Transaction<*>?>,
    val categorizedExpenses: List<Transaction<*>>,
    val outgoingTransfers: List<Transaction<*>>,
    val incomingDeposits: List<Transaction<*>>,
    val warnings: List<String>,
    val month: YearMonth?,
)