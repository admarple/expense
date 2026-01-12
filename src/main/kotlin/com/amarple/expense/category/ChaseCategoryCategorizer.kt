package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction
import com.amarple.expense.read.report.ChaseTransaction

class ChaseCategoryCategorizer(
    private val chaseCategories: List<ChaseCategory>
): TransactionCategorizer {
    val categoryMap: Map<String, Category> = chaseCategories
        .associateBy(ChaseCategory::chaseCategory) { it.category }

    override fun <T : Transaction<*>> categorize(transaction: T): Category? {
        return if (transaction is ChaseTransaction) {
            categoryMap[transaction.chaseCategory]
        } else {
            transaction.category
        }
    }
}

data class ChaseCategory(
    val chaseCategory: String,
    val category: Category
)
