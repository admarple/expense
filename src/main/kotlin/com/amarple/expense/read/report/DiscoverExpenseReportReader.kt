package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.ExpenseReport
import com.amarple.expense.model.internal.PaymentInstrument
import com.amarple.expense.model.internal.Transaction
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File
import java.time.LocalDate

class DiscoverExpenseReportReader : ExpenseReportReader {
    override fun read(input: ExpenseReportInput): ExpenseReport<DiscoverTransaction> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(DiscoverExpenseLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        val expenseLines = csvMapper.readerFor(DiscoverExpenseLine::class.java)
            .with(schema)
            .readValues<DiscoverExpenseLine>(File(input.path))
            .asSequence()

        return ExpenseReport(
            retrievalDate = input.retrievalDate ?: LocalDate.now(),
            transactions = expenseLines
                .map { DiscoverTransaction(it, input.source) }
                .toList(),
        )
    }
}

data class DiscoverExpenseLine(
    @JsonProperty("Trans. Date")
    @JsonFormat(pattern = "M/d/uu")
    val transactionDate: LocalDate,
    @JsonProperty("Post Date")
    @JsonFormat(pattern = "M/d/uu")
    val localDate: LocalDate,
    @JsonProperty("Description")
    val description: String? = null,
    /**
     * In Discover's CSV, a positive amount represents a charge. A negative amount represents a payment or credit.
     */
    @JsonProperty("Amount")
    val amount: Double,
    @JsonProperty("Category")
    val discoverCategory: String? = null,
)

data class DiscoverTransaction(
    val transactionDate: LocalDate,
    val postDate: LocalDate,
    override val amount: Double,
    override val description: String,
    val discoverCategory: String? = null,
    override val category: Category? = null,
    override val instrument: PaymentInstrument? = null,
): Transaction<DiscoverTransaction> {
    override val date: LocalDate = transactionDate

    constructor(discoverExpenseLine: DiscoverExpenseLine, source: String? = null): this(
        transactionDate = discoverExpenseLine.transactionDate,
        postDate = discoverExpenseLine.localDate,
        // Discover's CSV uses positive amounts for debts, so we must negate the amount
        amount = discoverExpenseLine.amount * -1,
        description = discoverExpenseLine.description ?: "",
        discoverCategory = discoverExpenseLine.discoverCategory,
        instrument = source?.let { PaymentInstrument(it) },
    )

    override fun updateCategory(category: Category?): DiscoverTransaction {
        return copy(category = category)
    }
}