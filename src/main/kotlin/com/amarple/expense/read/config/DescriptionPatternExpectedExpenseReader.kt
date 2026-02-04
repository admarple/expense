package com.amarple.expense.read.config

import com.amarple.expense.expected.DescriptionPatternExpectedExpense
import com.amarple.expense.model.DescriptionPatternExpectedExpenseLine
import com.amarple.expense.model.DescriptionPatternExpectedExpensesInput
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File

class DescriptionPatternExpectedExpenseReader {
    fun read(input: DescriptionPatternExpectedExpensesInput): List<DescriptionPatternExpectedExpense> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(DescriptionPatternExpectedExpenseLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        return csvMapper.readerFor(DescriptionPatternExpectedExpenseLine::class.java)
            .with(schema)
            .readValues<DescriptionPatternExpectedExpenseLine>(File(input.path))
            .asSequence()
            .map { it.toDescriptionPattern() }
            .toList()
    }
}