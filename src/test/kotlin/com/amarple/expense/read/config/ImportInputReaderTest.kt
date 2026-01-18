package com.amarple.expense.read.config

import com.amarple.expense.model.AggregationInput
import com.amarple.expense.read.Jackson.mapper
import com.amarple.expense.model.ImportInput
import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.ExpectedExpensesInput
import com.amarple.expense.model.DescriptionPatternExpectedExpensesInput
import com.amarple.expense.model.CategoriesInput
import com.amarple.expense.model.DateRange
import com.amarple.expense.model.DescriptionPatternCategoryInput
import com.amarple.expense.model.ExpenseReportType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate

class ImportInputReaderTest {

    @Test
    fun `should read ImportInput from JSON`() {
        val expectedImportInput = ImportInput(
            reports = listOf(ExpenseReportInput("path/to/report", "Source", reportType = ExpenseReportType.BankOfAmerica)),
            expectedExpenses = ExpectedExpensesInput("path/to/expected", DescriptionPatternExpectedExpensesInput("path/to/patterns")),
            categories = CategoriesInput(DescriptionPatternCategoryInput("path/to/category/patterns")),
            aggregation = AggregationInput(dateRange = DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 31)))
        )

        val tempFile = File.createTempFile("import_input", ".json")
        mapper.writeValue(tempFile, expectedImportInput)

        val reader = ImportInputReader()
        val result = reader.read(tempFile.absolutePath)

        assertEquals(expectedImportInput.reports.size, result.reports.size)
        assertEquals(expectedImportInput.reports[0].path, result.reports[0].path)
        assertEquals(expectedImportInput.reports[0].reportType, result.reports[0].reportType)
        assertEquals(expectedImportInput.expectedExpenses.path, result.expectedExpenses.path)

        tempFile.delete()
    }
}
