package com.amarple.expense.model

import com.amarple.expense.read.report.ExpenseReportType
import java.time.LocalDate

/**
 * TODO: change properties to val if it plays nicely with Jackson serializer
 */
data class ImportInput(
    val reports: List<ExpenseReportInput>,
    val expectedExpenses: ExpectedExpensesInput,
    val categories: CategoriesInput,
)

data class ExpenseReportInput(
    val path: String,
    val source: String,
    val retrievalDate: LocalDate? = null,
    val reportType: ExpenseReportType,
)

data class ExpectedExpensesInput(
    val path: String,
    val descriptionPatterns: DescriptionPatternExpectedExpensesInput,
)

data class DescriptionPatternExpectedExpensesInput(
    val path: String,
)

data class CategoriesInput(
    // val path: String,
    val descriptionPatterns: DescriptionPatternCategoryInput,
)

data class DescriptionPatternCategoryInput(
    val path: String,
)