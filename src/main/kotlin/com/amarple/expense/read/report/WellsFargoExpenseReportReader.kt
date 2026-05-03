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
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDate

class WellsFargoExpenseReportReader(
    val v1ReportReader: WellsFargoV1ExpenseReportReader = WellsFargoV1ExpenseReportReader(),
    val v2ReportReader: WellsFargoV2ExpenseReportReader = WellsFargoV2ExpenseReportReader(),
) : ExpenseReportReader {
    private val logger = LoggerFactory.getLogger(WellsFargoExpenseReportReader::class.java)

    override fun read(input: ExpenseReportInput): ExpenseReport<WellsFargoTransaction> {
        return listOf(v2ReportReader, v1ReportReader).firstNotNullOfOrNull { reportReader ->
            try {
                @Suppress("UNCHECKED_CAST")
                reportReader.read(input) as ExpenseReport<WellsFargoTransaction>
            } catch (e: Exception) {
                logger.warn("Exception reading report using ${reportReader.javaClass}", e)
                null
            }
        } ?: throw RuntimeException("Could not read expense report, all reporters failed.")
    }
}

class WellsFargoV1ExpenseReportReader : ExpenseReportReader {
    override fun read(input: ExpenseReportInput): ExpenseReport<WellsFargoTransaction> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(WellsFargoV1ExpenseLine::class.java)
            .withoutHeader()

        val expenseLines = csvMapper.readerFor(WellsFargoV1ExpenseLine::class.java)
            .with(schema)
            .readValues<WellsFargoV1ExpenseLine>(File(input.path))
            .asSequence()

        return ExpenseReport(
            retrievalDate = input.retrievalDate ?: LocalDate.now(),
            transactions = expenseLines
                .map { WellsFargoTransaction(it, input.source) }
                .toList(),
        )
    }
}

class WellsFargoV2ExpenseReportReader : ExpenseReportReader {
    override fun read(input: ExpenseReportInput): ExpenseReport<WellsFargoTransaction> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(WellsFargoV2ExpenseLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        val expenseLines = csvMapper.readerFor(WellsFargoV2ExpenseLine::class.java)
            .with(schema)
            .readValues<WellsFargoV2ExpenseLine>(File(input.path))
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
data class WellsFargoV1ExpenseLine(
    @JsonProperty("Date")
    @JsonFormat(pattern = "MM/dd/yyyy")
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

@JsonPropertyOrder("DATE", "DESCRIPTION", "AMOUNT", "CHECK #", "STATUS")
data class WellsFargoV2ExpenseLine(
    @JsonProperty("DATE")
    @JsonFormat(pattern = "MM/dd/yyyy")
    val date: LocalDate,
    @JsonProperty("AMOUNT")
    val amount: Double,
    @JsonProperty("CHECK #")
    val checkNumber: String? = null,
    @JsonProperty("STATUS")
    val status: String? = null,
    @JsonProperty("DESCRIPTION")
    val description: String? = null,
)

data class WellsFargoTransaction(
    override val date: LocalDate,
    override val amount: Double,
    override val description: String,
    override val category: Category? = null,
    override val instrument: PaymentInstrument? = null,
): Transaction<WellsFargoTransaction> {
    constructor(wellsFargoV1ExpenseLine: WellsFargoV1ExpenseLine, source: String? = null): this(
        date = wellsFargoV1ExpenseLine.date,
        amount = wellsFargoV1ExpenseLine.amount,
        description = wellsFargoV1ExpenseLine.description ?: "",
        instrument = source?.let { PaymentInstrument(it) },
    )

    constructor(wellsFargoV2ExpenseLine: WellsFargoV2ExpenseLine, source: String? = null): this(
        date = wellsFargoV2ExpenseLine.date,
        amount = wellsFargoV2ExpenseLine.amount,
        description = wellsFargoV2ExpenseLine.description ?: "",
        instrument = source?.let { PaymentInstrument(it) },
    )

    override fun updateCategory(category: Category?): WellsFargoTransaction {
        return copy(category = category)
    }
}