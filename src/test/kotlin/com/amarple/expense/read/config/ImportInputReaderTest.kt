package com.amarple.expense.read.config

import com.amarple.expense.read.Jackson.mapper
import com.amarple.expense.model.ImportInput
import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.ExpectedExpensesInput
import com.amarple.expense.model.DescriptionPatternExpectedExpensesInput
import com.amarple.expense.model.CategoriesInput
import com.amarple.expense.model.DescriptionPatternCategoryInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class ImportInputReaderTest {

    @Test
    fun `should read ImportInput from JSON`() {
        val importInput = ImportInput(
            reports = listOf(ExpenseReportInput("path/to/report", "Source")),
            expectedExpenses = ExpectedExpensesInput("path/to/expected", DescriptionPatternExpectedExpensesInput("path/to/patterns")),
            categories = CategoriesInput("path/to/categories", DescriptionPatternCategoryInput("path/to/category/patterns"))
        )

        val tempFile = File.createTempFile("import_input", ".json")
        mapper.writeValue(tempFile, importInput)

        val reader = ImportInputReader()
        val result = reader.read(tempFile.absolutePath)

        assertEquals(importInput.reports.size, result.reports.size)
        assertEquals(importInput.reports[0].path, result.reports[0].path)
        assertEquals(importInput.expectedExpenses?.path, result.expectedExpenses?.path)

        tempFile.delete()
    }
}
