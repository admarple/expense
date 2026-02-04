package com.amarple.expense.model

import com.amarple.expense.category.AmExCategory
import com.amarple.expense.category.CapitalOneCategory
import com.amarple.expense.category.ChaseCategory
import com.amarple.expense.category.DescriptionPatternCategory
import com.amarple.expense.category.DiscoverCategory
import com.amarple.expense.expected.DescriptionPatternExpectedExpense
import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.Category
import com.amarple.expense.model.internal.ExpectedExpense
import com.amarple.expense.model.internal.PaymentInstrument
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
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
    CapitalOneChecking,
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

data class CapitalOneCategoryLine(
    @JsonProperty("capitalOneCategory")
    val capitalOneCategory: String,
    @JsonProperty("category")
    val category: String,
    @JsonProperty("subcategory")
    val subcategory: String,
) {
    fun buildCategory() = Category(category, subcategory)

    fun toCapitalOneCategory() = CapitalOneCategory(capitalOneCategory, buildCategory())
}

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

data class DescriptionPatternExpectedExpenseLine(
    @JsonProperty("pattern")
    val pattern: String,
    @JsonProperty("transactionName")
    val transactionName: String,
    @JsonProperty("requirePriceMatch")
    val requirePriceMatch: Boolean = false
) {
    fun toDescriptionPattern() = DescriptionPatternExpectedExpense(pattern, transactionName, requirePriceMatch)
}

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

/**
 * TODO: move to the model package (since this is part of the input)?
 */
data class UberSheetExpectedExpense(
    @JsonProperty("Category")
    val category: String? = null,
    @JsonProperty("Subcategory")
    val subcategory: String? = null,
    @JsonProperty("Payment Instrument")
    val instrument: String? = null,
    @JsonProperty("Name")
    val name: String,
    @JsonProperty("Amount")
    val amount: Double? = null,
) {
    fun toExpectedExpense(): ExpectedExpense<*> {
        return ExpectedExpense(
            name = name,
            expectedTransaction = BasicTransaction(
                date = LocalDate.now(),
                amount = amount ?: 0.0,
                description = name,
                category = if (category != null && subcategory != null) Category(category, subcategory) else null,
                instrument = if (instrument != null) PaymentInstrument(instrument) else null,
            )
        )
    }
}