package com.amarple.expense.category

import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction
import java.time.LocalDate

class DiscretionaryTransactionsBeautifier() {
    fun beautifyForCategory(
        transactions: List<Transaction<*>>,
        allCategoriesInOrder: List<Category>
    ): List<Transaction<*>> {
        return allCategoriesInOrder.flatMap { category ->
            val categoryTransactions = transactions.filter { it.category == category }
            categoryTransactions.ifEmpty {
                listOf(
                    BasicTransaction(
                        description = "No transactions found",
                        category = category,
                        date = LocalDate.now(),
                        amount = 0.0,
                        instrument = null,
                    )
                )
            }
        }
    }
}