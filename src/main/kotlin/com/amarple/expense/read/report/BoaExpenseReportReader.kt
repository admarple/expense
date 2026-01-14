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

class BoaExpenseReportReader : ExpenseReportReader {
    override fun read(input: ExpenseReportInput): ExpenseReport<BoaTransaction> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(BoaExpenseLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        val expenseLines = csvMapper.readerFor(BoaExpenseLine::class.java)
            .with(schema)
            .readValues<BoaExpenseLine>(File(input.path))
            .asSequence()

        return ExpenseReport(
            retrievalDate = input.retrievalDate ?: LocalDate.now(),
            transactions = expenseLines
                .map { BoaTransaction(it, input.source) }
                .toList(),
        )
    }
}

data class BoaExpenseLine(
    @JsonProperty("Posted Date")
    @JsonFormat(pattern = "MM/dd/yyyy")
    val date: LocalDate,
    @JsonProperty("Reference Number")
    val referenceNumber: String? = null,
    @JsonProperty("Payee")
    val payee: String? = null,
    @JsonProperty("Address")
    val address: String? = null,
    @JsonProperty("Amount")
    val amount: Double,
)

data class BoaTransaction(
    override val date: LocalDate,
    val referenceNumber: String?,
    val payee: String,
    val address: String?,
    override val amount: Double,
    override val category: Category? = null,
    override val instrument: PaymentInstrument? = null,
): Transaction<BoaTransaction> {
    override val description: String = payee

    constructor(boaExpenseLine: BoaExpenseLine, source: String? = null): this(
        date = boaExpenseLine.date,
        referenceNumber = boaExpenseLine.referenceNumber,
        payee = boaExpenseLine.payee ?: "",
        address = boaExpenseLine.address,
        amount = boaExpenseLine.amount,
        instrument = source?.let { PaymentInstrument(it) },
    )

    override fun updateCategory(category: Category?): BoaTransaction {
        return copy(category = category)
    }
}