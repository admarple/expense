package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.ExpenseReport
import com.amarple.expense.model.internal.PaymentInstrument
import com.amarple.expense.model.internal.Transaction
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File
import java.time.LocalDate

class CapitalOneExpenseReportReader : ExpenseReportReader {
    override fun read(input: ExpenseReportInput): ExpenseReport<CapitalOneTransaction> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(CapitalOneExpenseLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        val expenseLines = csvMapper.readerFor(CapitalOneExpenseLine::class.java)
            .with(schema)
            .readValues<CapitalOneExpenseLine>(File(input.path))
            .asSequence()

        return ExpenseReport(
            retrievalDate = input.retrievalDate ?: LocalDate.now(),
            transactions = expenseLines
                .map { CapitalOneTransaction(it, input.source) }
                .toList(),
        )
    }
}

// Ignore fields that we don't need, such as "Card No."
@JsonIgnoreProperties(ignoreUnknown = true)
data class CapitalOneExpenseLine(
    @JsonProperty("Transaction Date")
    @JsonFormat(pattern = "M/d/uu")
    val transactionDate: LocalDate,
    @JsonProperty("Posted Date")
    @JsonFormat(pattern = "M/d/uu")
    val postedDate: LocalDate,
    @JsonProperty("Description")
    val description: String? = null,
    @JsonProperty("Category")
    val capitalOneCategory: String? = null,
    @JsonProperty("Debit")
    val debit: Double? = null,
    @JsonProperty("Credit")
    val credit: Double? = null,
) {
    val amount: Double = (credit ?: 0.0) - (debit ?: 0.0)
}

data class CapitalOneTransaction(
    override val date: LocalDate,
    override val description: String,
    val capitalOneCategory: String?,
    override val amount: Double,
    override val category: Category? = null,
    override val instrument: PaymentInstrument? = null,
): Transaction<CapitalOneTransaction> {
    constructor(capitalOneExpenseLine: CapitalOneExpenseLine, source: String? = null): this(
        date = capitalOneExpenseLine.transactionDate,
        description = capitalOneExpenseLine.description ?: "",
        capitalOneCategory = capitalOneExpenseLine.capitalOneCategory,
        amount = capitalOneExpenseLine.amount,
        instrument = source?.let { PaymentInstrument(it) },
    )

    override fun updateCategory(category: Category?): CapitalOneTransaction {
        return copy(category = category)
    }
}