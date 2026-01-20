package com.amarple.expense.read.config

import com.amarple.expense.model.CategoryHierarchyInput
import com.amarple.expense.model.internal.Category
import com.amarple.expense.read.Jackson.csvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import java.io.File

class CategoryHierarchyReader {
    fun read(input: CategoryHierarchyInput): List<Category> {
        val lines = csvMapper.readerForListOf(String::class.java)
            .with(CsvParser.Feature.WRAP_AS_ARRAY)
            .readValues<List<String>>(File(input.path))
            .readAll()
            .toList()

        val categoryValues = lines[0]
        val subcategoryLines = lines.slice(1..<lines.size)
        val categories = categoryValues.flatMapIndexed { index, categoryValue ->
            return@flatMapIndexed if (!categoryValue.isNullOrBlank()) {
                subcategoryLines.mapNotNull { line ->
                    val subcategoryValue = line[index]
                    return@mapNotNull if (!subcategoryValue.isNullOrBlank()) {
                        Category(categoryValue, line[index])
                    } else {
                        null
                    }
                }
            } else {
                emptyList()
            }
        }

        return categories
    }
}