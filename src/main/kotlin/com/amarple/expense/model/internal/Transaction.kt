package com.amarple.expense.model.internal

import java.time.LocalDate

interface Transaction<T: Transaction<T>> {
    val date: LocalDate
    /**
     * A negative amount represents something owed, spent, or lost. A positive amount represents something received or earned.
     */
    val amount: Double
    val description: String
    val category: Category?
    val instrument: PaymentInstrument?

    fun updateCategory(category: Category?): T
}

data class BasicTransaction(
    override val date: LocalDate,
    override val amount: Double,
    override val description: String,
    override val category: Category?,
    override val instrument: PaymentInstrument?,
) : Transaction<BasicTransaction> {
    override fun updateCategory(category: Category?): BasicTransaction {
        return copy(category = category)
    }
}

data class AggregatedTransaction(
    val transactions: List<Transaction<*>> = emptyList(),
    override val description: String = "aggregated transactions: ${transactions.size}"
) : Transaction<AggregatedTransaction> {
    override val date: LocalDate = transactions.maxBy { it.date }.date
    override val amount: Double = transactions.sumOf { it.amount }
    override val instrument: PaymentInstrument? = transactions.map { it.instrument }.firstOrNull()
    override val category: Category? = transactions.map { it.category }.firstOrNull()

    override fun updateCategory(category: Category?): AggregatedTransaction {
        val transactions = transactions
        val transactionsWithCategory = transactions.map { it.updateCategory(category) }
        return copy(transactions = transactionsWithCategory)
    }
}