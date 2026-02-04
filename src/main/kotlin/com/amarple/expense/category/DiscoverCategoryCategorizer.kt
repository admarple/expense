package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction
import com.amarple.expense.read.report.DiscoverTransaction
import kotlin.collections.get

class DiscoverCategoryCategorizer(
    private val discoverCategories: List<DiscoverCategory>
): TransactionCategorizer {
    val categoryMap: Map<String, Category> = discoverCategories
        .associateBy(DiscoverCategory::discoverCategory) { it.category }

    override fun <T : Transaction<*>> categorize(transaction: T): Category? {
        return if (transaction is DiscoverTransaction) {
            categoryMap[transaction.discoverCategory]
        } else {
            // We preserve the existing category here ...
            // ... but if we want to indicate that there isn't a matching DiscoverCategory, then maybe we should return null
            transaction.category
        }
    }
}

data class DiscoverCategory(
    val discoverCategory: String,
    val category: Category
)