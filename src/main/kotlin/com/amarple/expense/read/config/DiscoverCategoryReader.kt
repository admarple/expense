package com.amarple.expense.read.config

import com.amarple.expense.category.DiscoverCategory
import com.amarple.expense.model.DiscoverCategoryInput
import com.amarple.expense.model.internal.Category
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.annotation.JsonProperty
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

/**
 * TODO: move to the model package (since this is part of the input)?
 */
data class DiscoverCategoryLine(
    @JsonProperty("discoverCategory")
    val discoverCategory: String,
    @JsonProperty("category")
    val category: String,
    @JsonProperty("subcategory")
    val subcategory: String,
) {
    fun buildCategory() = Category(category, subcategory)

    fun toDiscoverCategory() = DiscoverCategory(discoverCategory, buildCategory())
}