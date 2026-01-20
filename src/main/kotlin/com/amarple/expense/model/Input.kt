package com.amarple.expense.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class ImportInput(
    val reports: List<ExpenseReportInput>,
    val expectedExpenses: ExpectedExpensesInput,
    val categories: CategoriesInput,
    val aggregation: AggregationInput = AggregationInput(),
)

data class ExpenseReportInput(
    val path: String,
    val source: String,
    val retrievalDate: LocalDate? = null,
    val reportType: ExpenseReportType,
)

enum class ExpenseReportType {
    BankOfAmerica,
    Discover,
    WellsFargo,
    AmericanExpress,
    Chase,
    CapitalOne,
}

data class ExpectedExpensesInput(
    val path: String,
    val descriptionPatterns: DescriptionPatternExpectedExpensesInput,
)

data class DescriptionPatternExpectedExpensesInput(
    val path: String,
)

data class CategoriesInput(
    val hierarchy: CategoryHierarchyInput? = null,
    val descriptionPatterns: DescriptionPatternCategoryInput,
    val amExCategories: AmExCategoryInput? = null,
    val capitalOneCategories: CapitalOneCategoryInput? = null,
    val chaseCategories: ChaseCategoryInput? = null,
    val discoverCategories: DiscoverCategoryInput? = null,
)

data class DescriptionPatternCategoryInput(
    val path: String,
)

data class CategoryHierarchyInput(
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

data class AggregationInput(
    val aggregationType: AggregationType? = null,
    val dateRange: DateRange? = null,
)

enum class AggregationType {
    Nothing,
    Category,
    Instrument,
    CategoryAndInstrument,
}

data class DateRange(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate,
)