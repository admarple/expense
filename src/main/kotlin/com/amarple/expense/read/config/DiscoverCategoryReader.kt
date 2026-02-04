package com.amarple.expense.read.config

import com.amarple.expense.category.DiscoverCategory
import com.amarple.expense.model.DiscoverCategoryInput
import com.amarple.expense.model.DiscoverCategoryLine
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File

class DiscoverCategoryReader {
    fun read(input: DiscoverCategoryInput): List<DiscoverCategory> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(DiscoverCategoryLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        return csvMapper.readerFor(DiscoverCategoryLine::class.java)
            .with(schema)
            .readValues<DiscoverCategoryLine>(File(input.path))
            .asSequence()
            .map { it.toDiscoverCategory() }
            .toList()
    }
}