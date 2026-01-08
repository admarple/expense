package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction

class DescriptionPatternCategorizer<T : Transaction>(
    private val patterns: List<DescriptionPatternCategory>
) : TransactionCategorizer<T> {
    private val regexes = patterns
        .map { Pair(Regex(it.pattern), it.category) }

    override fun categorize(transaction: T): Category? {
        return regexes
            .firstOrNull { it.first.matches(transaction.description) }
            ?.let { return it.second }
    }
}

data class DescriptionPatternCategory(
    val pattern: String,
    val category: Category
)