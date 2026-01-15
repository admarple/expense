package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.Transaction

class DescriptionPatternCategorizer(
    private val patterns: List<DescriptionPatternCategory>,
    private val descriptionPermuter: DescriptionPermuter = CompositeDescriptionPermuter(listOf(WellsFargoDescriptionPermuter())),
) : TransactionCategorizer {
    private val regexes = patterns
        .map { Pair(Regex(it.pattern), it.category) }

    override fun <T : Transaction<*>> categorize(transaction: T): Category? {
        return regexes
            .firstOrNull { regex -> descriptionPermuter.permute(transaction.description).any { regex.first.containsMatchIn(it) }  }
            ?.let { return it.second }
    }
}

data class DescriptionPatternCategory(
    val pattern: String,
    val category: Category
)

interface DescriptionPermuter {
    fun permute(pattern: String): List<String>
}

class CompositeDescriptionPermuter(
    private val permuters: List<DescriptionPermuter>
) : DescriptionPermuter {
    override fun permute(pattern: String): List<String> {
        return permuters.flatMap { it.permute(pattern) }
    }
}

class WellsFargoDescriptionPermuter(
    private val includeOriginalDescription: Boolean = true,
) : DescriptionPermuter {
    override fun permute(pattern: String): List<String> {
        return listOf(
            if (includeOriginalDescription) pattern else null,
            pattern.replace(PURCHASE_REGEX, "")
        ).filterNotNull()
    }

    companion object {
        private val PURCHASE_REGEX = Regex("^(PURCHASE AUTHORIZED ON [^ ]* )")
    }
}