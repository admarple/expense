package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction
import com.amarple.expense.read.report.CapitalOneTransaction

class CapitalOneCategoryCategorizer(
    private val capitalOneCategories: List<CapitalOneCategory>
): TransactionCategorizer {
    val categoryMap: Map<String, Category> = capitalOneCategories
        .associateBy(CapitalOneCategory::capitalOneCategory) { it.category }

    override fun <T : Transaction<*>> categorize(transaction: T): Category? {
        return if (transaction is CapitalOneTransaction) {
            categoryMap[transaction.capitalOneCategory]
        } else {
            // We preserve the existing category here ...
            // ... but if we want to indicate that there isn't a matching CapitalOneCategory, then maybe we should return null
            transaction.category
        }
    }
}

data class CapitalOneCategory(
    val capitalOneCategory: String,
    val category: Category
)