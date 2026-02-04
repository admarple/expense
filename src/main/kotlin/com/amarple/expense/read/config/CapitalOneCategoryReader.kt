package com.amarple.expense.read.config

import com.amarple.expense.category.CapitalOneCategory
import com.amarple.expense.model.CapitalOneCategoryInput
import com.amarple.expense.model.CapitalOneCategoryLine
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File

class CapitalOneCategoryReader {
    fun read(input: CapitalOneCategoryInput): List<CapitalOneCategory> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(CapitalOneCategoryLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        return csvMapper.readerFor(CapitalOneCategoryLine::class.java)
            .with(schema)
            .readValues<CapitalOneCategoryLine>(File(input.path))
            .asSequence()
            .map { it.toCapitalOneCategory() }
            .toList()
    }
}