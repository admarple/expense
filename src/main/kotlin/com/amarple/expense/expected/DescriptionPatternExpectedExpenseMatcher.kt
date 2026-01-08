package com.amarple.expense.expected

import com.amarple.expense.model.internal.ExpectedExpense
import com.amarple.expense.model.internal.Transaction

class DescriptionPatternExpectedExpenseMatcher<T : Transaction>(
    private val patterns: List<DescriptionPatternExpectedExpense>
) : ExpectedExpenseMatcher<T> {
    private val regexes = patterns
        .map { Pair(Regex(it.pattern), it.expenseName) }

    override fun isMatch(expectedExpense: ExpectedExpense, transaction: T): Boolean {
        return regexes.any { it.second == expectedExpense.name && it.first.matches(transaction.description) }
    }
}

data class DescriptionPatternExpectedExpense(
    val pattern: String,
    val expenseName: String
)