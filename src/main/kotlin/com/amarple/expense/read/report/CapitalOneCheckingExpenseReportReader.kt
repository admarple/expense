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

class CapitalOneCheckingExpenseReportReader : ExpenseReportReader {
    override fun read(input: ExpenseReportInput): ExpenseReport<CapitalOneCheckingTransaction> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(CapitalOneCheckingExpenseLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        val expenseLines = csvMapper.readerFor(CapitalOneCheckingExpenseLine::class.java)
            .with(schema)
            .readValues<CapitalOneCheckingExpenseLine>(File(input.path))
            .asSequence()

        return ExpenseReport(
            retrievalDate = input.retrievalDate ?: LocalDate.now(),
            transactions = expenseLines
                .map { CapitalOneCheckingTransaction(it, input.source) }
                .toList(),
        )
    }
}

// Ignore fields that we don't need, such as "Card No."
@JsonIgnoreProperties(ignoreUnknown = true)
data class CapitalOneCheckingExpenseLine(
    @JsonProperty("Transaction Description")
    val description: String? = null,
    @JsonProperty("Transaction Date")
    @JsonFormat(pattern = "MM/dd/yy")
    val transactionDate: LocalDate,
    @JsonProperty("Transaction Type")
    val transactionType: String? = null,
    @JsonProperty("Transaction Amount")
    val transactionAmount: Double? = null,
    @JsonProperty("Balance")
    val balance: Double? = null,
) {
    val amount: Double = (if (transactionType == "Debit") {
        transactionAmount?.times(-1)
    } else {
        transactionAmount
    }) ?: 0.0
}

data class CapitalOneCheckingTransaction(
    override val date: LocalDate,
    override val description: String,
    override val amount: Double,
    override val category: Category? = null,
    override val instrument: PaymentInstrument? = null,
): Transaction<CapitalOneCheckingTransaction> {
    constructor(capitalOneExpenseLine: CapitalOneCheckingExpenseLine, source: String? = null): this(
        date = capitalOneExpenseLine.transactionDate,
        description = capitalOneExpenseLine.description ?: "",
        amount = capitalOneExpenseLine.amount,
        instrument = source?.let { PaymentInstrument(it) },
    )

    override fun updateCategory(category: Category?): CapitalOneCheckingTransaction {
        return copy(category = category)
    }
}