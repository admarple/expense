package com.amarple.expense.model

import com.amarple.expense.read.report.ExpenseReportType
import java.time.LocalDate

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
    val amExCategories: AmExCategoryInput? = null,
    val capitalOneCategories: CapitalOneCategoryInput? = null,
    val chaseCategories: ChaseCategoryInput? = null,
    val discoverCategories: DiscoverCategoryInput? = null,
)

data class DescriptionPatternCategoryInput(
    val path: String,
)

data class AmExCategoryInput(
    val path: String,
)

data class CapitalOneCategoryInput(
    val path: String,
)

data class ChaseCategoryInput(
    val path: String,
)

data class DiscoverCategoryInput(
    val path: String,
)