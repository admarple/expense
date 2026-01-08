package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.ExpenseReport
import com.amarple.expense.model.internal.PaymentInstrument
import com.amarple.expense.model.internal.Transaction
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File
import java.time.LocalDate

class DiscoverExpenseReportReader : ExpenseReportReader {
    override fun read(input: ExpenseReportInput): ExpenseReport {
        val schema: CsvSchema = csvMapper.schemaFor(DiscoverExpenseLine::class.java)
        val expenseLines = csvMapper.reader()
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
    @JsonProperty("Trans. Date") var transactionDate: String,
    @JsonProperty("Post Date") var localDate: String,
    @JsonProperty("Description") var description: String? = null,
    /**
     * In Discover's CSV, a positive amount represents a charge. A negative amount represents a payment or credit.
     */
    @JsonProperty("Amount") var amount: Double,
    @JsonProperty("Category") var discoverCategoryName: String? = null,
)

enum class DiscoverCategory(val displayName: String?) {
    UNRECOGNIZED(null),
    MERCHANDISE("Merchandise"),
    SERVICES("Services"),
    PAYMENTS_AND_CREDITS("Payments and credits"),
    TRAVEL_AND_ENTERTAINMENT("Travel/ Entertainment"),
    RESTAURANTS("Restaurants"),
    SUPERMARKETS("Supermarkets"),
    GOVERNMENT_SERVICES("Government Services"),
    AWARDS_AND_REBATE_CREDITS("Awards and Rebate Credits");

    companion object {
        fun lookupByDisplayName(displayName: String?): DiscoverCategory {
            return DiscoverCategory.entries.find { it.displayName == displayName } ?: DiscoverCategory.UNRECOGNIZED
        }
    }
}

data class DiscoverTransaction(
    val transactionDate: LocalDate,
    val postDate: LocalDate,
    override val amount: Double,
    override val description: String,
    val discoverCategoryName: String? = null,
    override val category: Category? = null,
    override val instrument: PaymentInstrument? = null,
): Transaction {
    override val date: LocalDate = transactionDate
    val discoverCategory: DiscoverCategory = DiscoverCategory.lookupByDisplayName(discoverCategoryName)

    constructor(discoverExpenseLine: DiscoverExpenseLine, source: String? = null): this(
        transactionDate = LocalDate.parse(discoverExpenseLine.transactionDate),
        postDate = LocalDate.parse(discoverExpenseLine.localDate),
        // Discover's CSV uses positive amounts for debts, so we must negate the amount
        amount = discoverExpenseLine.amount * -1,
        description = discoverExpenseLine.description ?: "",
        discoverCategoryName = discoverExpenseLine.discoverCategoryName,
        instrument = source?.let { PaymentInstrument(it) },
    )
}