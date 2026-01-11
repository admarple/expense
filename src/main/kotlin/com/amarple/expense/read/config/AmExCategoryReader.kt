package com.amarple.expense.read.config

import com.amarple.expense.category.AmExCategory
import com.amarple.expense.model.AmExCategoryInput
import com.amarple.expense.model.internal.Category
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.annotation.JsonProperty
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

/**
 * TODO: move to the model package (since this is part of the input)?
 */
data class AmExCategoryLine(
    @JsonProperty("amExCategory")
    val amExCategory: String,
    @JsonProperty("amExSubcategory")
    val amExSubcategory: String?,
    @JsonProperty("category")
    val category: String,
    @JsonProperty("subcategory")
    val subcategory: String,
) {
    fun buildCategory() = Category(category, subcategory)

    fun toAmExCategory() = AmExCategory(amExCategory, amExSubcategory, buildCategory())
}