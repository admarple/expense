package com.amarple.expense.read.config

import com.amarple.expense.category.AmExCategory
import com.amarple.expense.model.AmExCategoryInput
import com.amarple.expense.model.AmExCategoryLine
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File

class AmExCategoryReader {
    fun read(input: AmExCategoryInput): List<AmExCategory> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(AmExCategoryLine::class.java)
            .withNullValue("")
            .withHeader()
            .withColumnReordering(true)

        return csvMapper.readerFor(AmExCategoryLine::class.java)
            .with(schema)
            .readValues<AmExCategoryLine>(File(input.path))
            .asSequence()
            .map { it.toAmExCategory() }
            .toList()
    }
}