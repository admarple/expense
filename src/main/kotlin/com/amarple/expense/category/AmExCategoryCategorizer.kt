package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction
import com.amarple.expense.read.report.AmExTransaction

class AmExCategoryCategorizer(
    private val amExCategories: List<AmExCategory>
): TransactionCategorizer {
    val amExCategoryMap: Map<String, Map<String?, Category>> = amExCategories
        .groupBy(AmExCategory::amExCategory)
        .mapValues { it.value.associateBy(AmExCategory::amExSubcategory) { it.category } }

    override fun <T : Transaction<*>> categorize(transaction: T): Category? {
        return if (transaction is AmExTransaction) {
            val category = getCategory(transaction.amExCategory)
            val subcategory = getSubcategory(transaction.amExCategory)

            val subcategoryMap = amExCategoryMap[category]
            subcategoryMap?.get(subcategory) ?: subcategoryMap?.get(null)
        } else {
            // TODO: I *think* preserving the existing category here is best ...
            // TODO: ... but if we want to indicate that there isn't a matching DiscoveryCategory, then maybe we should return null
            transaction.category
        }
    }

    private fun getCategory(displayName: String?): String? {
        return displayName?.let { splitCategory(displayName).first }
    }

    private fun getSubcategory(displayName: String?): String? {
        return displayName?.let { splitCategory(displayName).second }
    }

    private fun splitCategory(displayName: String): Pair<String, String> {
        return displayName.split("-", limit = 2)
            .let { Pair(it[0], it[1]) }
    }
}

data class AmExCategory(
    val amExCategory: String,
    val amExSubcategory: String?,
    val category: Category
)