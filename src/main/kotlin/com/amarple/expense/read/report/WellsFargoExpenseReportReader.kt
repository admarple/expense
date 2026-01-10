package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.ExpenseReport
import com.amarple.expense.model.internal.PaymentInstrument
import com.amarple.expense.model.internal.Transaction
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File
import java.time.LocalDate

class WellsFargoExpenseReportReader : ExpenseReportReader {
    override fun read(input: ExpenseReportInput): ExpenseReport<WellsFargoTransaction> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(WellsFargoExpenseLine::class.java)
            .withoutHeader()

        val expenseLines = csvMapper.readerFor(WellsFargoExpenseLine::class.java)
            .with(schema)
            .readValues<WellsFargoExpenseLine>(File(input.path))
            .asSequence()

        return ExpenseReport(
            retrievalDate = input.retrievalDate ?: LocalDate.now(),
            transactions = expenseLines
                .map { WellsFargoTransaction(it, input.source) }
                .toList(),
        )
    }
}

@JsonPropertyOrder("Date", "Amount", "Huh? Always Star", "Huh? Always Blank", "Description")
data class WellsFargoExpenseLine(
    @JsonProperty("Date")
    @JsonFormat(pattern = "M/d/uu")
    val date: LocalDate,
    @JsonProperty("Amount")
    val amount: Double,
    @JsonProperty("Huh? Always Star")
    val huhAlwaysStar: String? = null, // TODO: what is this column supposed to be?
    @JsonProperty("Huh? Always Blank")
    val huhAlwaysBlank: String? = null, // TODO: what is this column supposed to be?
    @JsonProperty("Description")
    val description: String? = null,
)

data class WellsFargoTransaction(
    override val date: LocalDate,
    override val amount: Double,
    override val description: String,
    override val category: Category? = null,
    override val instrument: PaymentInstrument? = null,
): Transaction<WellsFargoTransaction> {
    constructor(wellsFargoExpenseLine: WellsFargoExpenseLine, source: String? = null): this(
        date = wellsFargoExpenseLine.date,
        amount = wellsFargoExpenseLine.amount,
        description = wellsFargoExpenseLine.description ?: "",
        instrument = source?.let { PaymentInstrument(it) },
    )

    override fun updateCategory(category: Category?): WellsFargoTransaction {
        return copy(category = category)
    }
}