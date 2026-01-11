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

class AmExExpenseReportReader : ExpenseReportReader {
    override fun read(input: ExpenseReportInput): ExpenseReport<AmExTransaction> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(AmExExpenseLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        val expenseLines = csvMapper.readerFor(AmExExpenseLine::class.java)
            .with(schema)
            .readValues<AmExExpenseLine>(File(input.path))
            .asSequence()

        return ExpenseReport(
            retrievalDate = input.retrievalDate ?: LocalDate.now(),
            transactions = expenseLines
                .map { AmExTransaction(it, input.source) }
                .toList(),
        )
    }
}

data class AmExExpenseLine(
    @JsonProperty("Date")
    @JsonFormat(pattern = "MM/dd/yyyy")
    val date: LocalDate,
    @JsonProperty("Description")
    val description: String? = null,
    /**
     * In AmEx's CSV, a positive amount represents a charge. A negative amount represents a payment or credit.
     */
    @JsonProperty("Amount")
    val amount: Double,
    @JsonProperty("Extended Details")
    val extendedDetails: String? = null,
    @JsonProperty("Appears On Your Statement As")
    val appearsAs: String? = null,
    @JsonProperty("Address")
    val address: String? = null,
    @JsonProperty("City/State")
    val cityState: String? = null,
    @JsonProperty("Zip Code")
    val zipCode: String? = null,
    @JsonProperty("Country")
    val country: String? = null,
    @JsonProperty("Reference")
    val reference: String? = null,
    @JsonProperty("Category")
    val amExCategory: String? = null,
)

data class AmExTransaction(
    override val date: LocalDate,
    override val description: String,
    override val amount: Double,
    val extendedDetails: String? = null,
    val appearsAs: String? = null,
    val address: String? = null,
    val cityState: String? = null,
    val zipCode: String? = null,
    val country: String? = null,
    val reference: String? = null,
    val amExCategory: String? = null,
    override val category: Category? = null,
    override val instrument: PaymentInstrument? = null,
): Transaction<AmExTransaction> {
    constructor(amExExpenseLine: AmExExpenseLine, source: String? = null): this(
        date = amExExpenseLine.date,
        description = amExExpenseLine.description ?: "",
        // AmEx's CSV uses positive amounts for debts, so we must negate the amount
        amount = amExExpenseLine.amount * -1,
        extendedDetails = amExExpenseLine.extendedDetails,
        appearsAs = amExExpenseLine.appearsAs,
        address = amExExpenseLine.address,
        cityState = amExExpenseLine.cityState,
        zipCode = amExExpenseLine.zipCode,
        country = amExExpenseLine.country,
        reference = amExExpenseLine.reference,
        amExCategory = amExExpenseLine.amExCategory,
        instrument = source?.let { PaymentInstrument(it) },
    )

    override fun updateCategory(category: Category?): AmExTransaction {
        return copy(category = category)
    }
}