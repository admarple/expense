package com.amarple.expense.read.config

import com.amarple.expense.expected.DescriptionPatternExpectedExpense
import com.amarple.expense.model.DescriptionPatternExpectedExpensesInput
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File

class DescriptionPatternExpectedExpenseReader {
    fun read(input: DescriptionPatternExpectedExpensesInput): List<DescriptionPatternExpectedExpense> {
        val schema: CsvSchema = csvMapper.schemaFor(DescriptionPatternExpectedExpenseLine::class.java)
        return csvMapper.reader()
            .with(schema)
            .readValues<DescriptionPatternExpectedExpenseLine>(File(input.path))
            .asSequence()
            .map { it.toDescriptionPattern() }
            .toList()
    }
}


/**
 * TODO: move to the model package (since this is part of the input)?
 */
data class DescriptionPatternExpectedExpenseLine(
    @JsonProperty("pattern") var pattern: String,
    @JsonProperty("transactionName") var transactionName: String,
) {
    fun toDescriptionPattern() = DescriptionPatternExpectedExpense(pattern, transactionName)
}