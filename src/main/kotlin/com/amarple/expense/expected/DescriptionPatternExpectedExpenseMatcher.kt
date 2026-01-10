package com.amarple.expense.expected

import com.amarple.expense.model.internal.ExpectedExpense
import com.amarple.expense.model.internal.Transaction

class DescriptionPatternExpectedExpenseMatcher(
    private val patterns: List<DescriptionPatternExpectedExpense>
) : ExpectedExpenseMatcher {
    private val regexes = patterns
        .map { Pair(Regex(it.pattern), it.expenseName) }

    override fun <E : Transaction<E>, T : Transaction<T>> isMatch(expectedExpense: ExpectedExpense<E>, transaction: T): Boolean {
        return regexes.any { it.second == expectedExpense.name && it.first.containsMatchIn(transaction.description) }
    }
}

data class DescriptionPatternExpectedExpense(
    val pattern: String,
    val expenseName: String
)