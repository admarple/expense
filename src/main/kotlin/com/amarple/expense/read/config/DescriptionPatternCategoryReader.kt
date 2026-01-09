package com.amarple.expense.read.config

import com.amarple.expense.category.DescriptionPatternCategory
import com.amarple.expense.model.DescriptionPatternCategoryInput
import com.amarple.expense.model.internal.Category
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File

class DescriptionPatternCategoryReader {
    fun read(input: DescriptionPatternCategoryInput): List<DescriptionPatternCategory> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(DescriptionPatternCategoryLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        return csvMapper.readerFor(DescriptionPatternCategoryLine::class.java)
            .with(schema)
            .readValues<DescriptionPatternCategoryLine>(File(input.path))
            .asSequence()
            .map { it.toDescriptionPattern() }
            .toList()
    }
}

/**
 * TODO: move to the model package (since this is part of the input)?
 */
data class DescriptionPatternCategoryLine(
    @JsonProperty("pattern")
    val pattern: String,
    @JsonProperty("category")
    val category: String,
    @JsonProperty("subcategory")
    val subcategory: String,
) {
    fun buildCategory() = Category(category, subcategory)

    fun toDescriptionPattern() = DescriptionPatternCategory(pattern, buildCategory())
}