package com.amarple.expense.read.expected

import com.amarple.expense.model.ExpectedExpensesInput
import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.ExpectedExpense
import com.amarple.expense.model.internal.PaymentInstrument
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File
import java.time.LocalDate

class UberSheetExpectedExpenseReader : ExpectedExpenseReader {
    override fun read(input: ExpectedExpensesInput): List<ExpectedExpense> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(UberSheetExpectedExpense::class.java)
            .withHeader()
            .withColumnReordering(true)

        return csvMapper.readerFor(UberSheetExpectedExpense::class.java)
            .with(schema)
            .readValues<UberSheetExpectedExpense>(File(input.path))
            .asSequence()
            .map { it.toExpectedExpense() }
            .toList()
    }
}

/**
 * TODO: move to the model package (since this is part of the input)?
 */
data class UberSheetExpectedExpense(
    @JsonProperty("Category")
    val category: String? = null,
    @JsonProperty("Subcategory")
    val subcategory: String? = null,
    @JsonProperty("Payment Instrument")
    val instrument: String? = null,
    @JsonProperty("Name")
    val name: String,
    @JsonProperty("Amount")
    val amount: Double? = null,
) {
    fun toExpectedExpense(): ExpectedExpense {
        return ExpectedExpense(
            name = name,
            expectedTransaction = BasicTransaction(
                date = LocalDate.now(),
                amount = amount ?: 0.0,
                description = name,
                category = if (category != null && subcategory != null) Category(category!!, subcategory!!) else null,
                instrument = if (instrument != null) PaymentInstrument(instrument!!) else null,
            )
        )
    }
}