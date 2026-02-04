package com.amarple.expense.common

interface DescriptionPermuter {
    fun permute(pattern: String): List<String>
}

object NoopDescriptionPermuter : DescriptionPermuter {
    override fun permute(pattern: String): List<String> {
        return listOf(pattern)
    }
}

class CompositeDescriptionPermuter(
    private val permuters: List<DescriptionPermuter>
) : DescriptionPermuter {
    override fun permute(pattern: String): List<String> {
        return permuters
            .flatMap { it.permute(pattern) }
            .distinct()
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

class CapitalOneCheckingDescriptionPermuter(
    private val includeOriginalDescription: Boolean = true,
) : DescriptionPermuter {
    override fun permute(pattern: String): List<String> {
        return listOf(
            if (includeOriginalDescription) pattern else null,
            pattern.replace(WITHDRAWAL_REGEX, ""),
            pattern.replace(DEPOSIT_REGEX, ""),
        ).filterNotNull()
    }

    companion object {
        private val WITHDRAWAL_REGEX = Regex("^(Withdrawal from )")
        private val DEPOSIT_REGEX = Regex("^(Deposit from )")
    }
}