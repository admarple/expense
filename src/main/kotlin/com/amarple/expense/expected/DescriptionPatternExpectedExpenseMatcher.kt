package com.amarple.expense.expected

import com.amarple.expense.model.internal.ExpectedExpense
import com.amarple.expense.model.internal.Transaction

class DescriptionPatternExpectedExpenseMatcher(
    private val patterns: List<DescriptionPatternExpectedExpense>
) : ExpectedExpenseMatcher {
    private val regexes = patterns
        .map { Pair(Regex(it.pattern), it) }

    override fun <E : Transaction<E>, T : Transaction<*>> isMatch(expectedExpense: ExpectedExpense<E>, transaction: T): Boolean {
        return regexes.any {
            it.second.expenseName == expectedExpense.name
                && it.first.containsMatchIn(transaction.description)
                && (!it.second.requirePriceMatch || expectedExpense.expectedTransaction.amount == transaction.amount)
        }
    }
}

data class DescriptionPatternExpectedExpense(
    val pattern: String,
    val expenseName: String,
    /**
     * If transactions cannot be identified by name alone, then only match when the amount is the same as the expected amount
     */
    val requirePriceMatch: Boolean = false,
)