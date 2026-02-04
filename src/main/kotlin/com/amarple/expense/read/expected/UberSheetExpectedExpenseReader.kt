package com.amarple.expense.read.expected

import com.amarple.expense.model.ExpectedExpensesInput
import com.amarple.expense.model.UberSheetExpectedExpense
import com.amarple.expense.model.internal.ExpectedExpense
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File

class UberSheetExpectedExpenseReader : ExpectedExpenseReader {
    override fun read(input: ExpectedExpensesInput): List<ExpectedExpense<*>> {
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

