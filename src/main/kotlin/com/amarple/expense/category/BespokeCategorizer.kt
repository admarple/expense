package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction

/**
 * This is the "top-level" categorizer for transactions. This class decides the order in which to try [[TransactionCategorizer]]s,
 * which to use when they conflict, etc.
 *
 * TODO: decide on a better name for this class
 */
class BespokeCategorizer(
    private val discoverCategoryCategorizer: DiscoverCategoryCategorizer,
    private val descriptionPatternCategorizer: DescriptionPatternCategorizer,
): TransactionCategorizer {
    override fun <T : Transaction<*>> categorize(transaction: T): Category? {
        return transaction.let {
            descriptionPatternCategorizer.categorize(it)
                ?: discoverCategoryCategorizer.categorize(it)
        }
    }
}