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

class ChaseExpenseReportReader : ExpenseReportReader {
    override fun read(input: ExpenseReportInput): ExpenseReport<ChaseTransaction> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(ChaseExpenseLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        val expenseLines = csvMapper.readerFor(ChaseExpenseLine::class.java)
            .with(schema)
            .readValues<ChaseExpenseLine>(File(input.path))
            .asSequence()

        return ExpenseReport(
            retrievalDate = input.retrievalDate ?: LocalDate.now(),
            transactions = expenseLines
                .map { ChaseTransaction(it, input.source) }
                .toList(),
        )
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChaseExpenseLine(
    @JsonProperty("Transaction Date")
    @JsonFormat(pattern = "M/d/uu")
    val transactionDate: LocalDate,
    @JsonProperty("Post Date")
    @JsonFormat(pattern = "M/d/uu")
    val postedDate: LocalDate,
    @JsonProperty("Description")
    val description: String? = null,
    @JsonProperty("Category")
    val chaseCategory: String? = null,
    @JsonProperty("Type")
    val type: String? = null,
    @JsonProperty("Amount")
    val amount: Double? = null,
    @JsonProperty("Memo")
    val memo: String? = null,
)

data class ChaseTransaction(
    override val date: LocalDate,
    override val description: String,
    val chaseCategory: String?,
    override val amount: Double,
    override val category: Category? = null,
    override val instrument: PaymentInstrument? = null,
): Transaction<ChaseTransaction> {
    constructor(chaseExpenseLine: ChaseExpenseLine, source: String? = null): this(
        date = chaseExpenseLine.transactionDate,
        description = chaseExpenseLine.description ?: "",
        chaseCategory = chaseExpenseLine.chaseCategory,
        amount = chaseExpenseLine.amount ?: 0.0,
        instrument = source?.let { PaymentInstrument(it) },
    )

    override fun updateCategory(category: Category?): ChaseTransaction {
        return copy(category = category)
    }
}