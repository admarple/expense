package com.amarple.expense.read.config

import com.amarple.expense.category.ChaseCategory
import com.amarple.expense.model.ChaseCategoryInput
import com.amarple.expense.model.internal.Category
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File

class ChaseCategoryReader {
    fun read(input: ChaseCategoryInput): List<ChaseCategory> {
        val schema: CsvSchema = csvMapper
            .typedSchemaFor(ChaseCategoryLine::class.java)
            .withHeader()
            .withColumnReordering(true)

        return csvMapper.readerFor(ChaseCategoryLine::class.java)
            .with(schema)
            .readValues<ChaseCategoryLine>(File(input.path))
            .asSequence()
            .map { it.toChaseCategory() }
            .toList()
    }
}

/**
 * TODO: move to the model package (since this is part of the input)?
 */
data class ChaseCategoryLine(
    @JsonProperty("chaseCategory")
    val chaseCategory: String,
    @JsonProperty("category")
    val category: String,
    @JsonProperty("subcategory")
    val subcategory: String,
) {
    fun buildCategory() = Category(category, subcategory)

    fun toChaseCategory() = ChaseCategory(chaseCategory, buildCategory())
}
