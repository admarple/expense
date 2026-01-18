package com.amarple.expense

import com.amarple.expense.category.AggregateBy
import com.amarple.expense.category.AggregateByCategory
import com.amarple.expense.category.AggregateByCategoryAndInstrument
import com.amarple.expense.category.AggregateByInstrument
import com.amarple.expense.category.AggregateNothing
import com.amarple.expense.category.AggregationSelector
import com.amarple.expense.cli.ImportCommandLineRunner
import com.amarple.expense.cli.ImportTask
import com.amarple.expense.model.AggregationType
import com.amarple.expense.model.ExpenseReportType
import com.amarple.expense.read.config.AmExCategoryReader
import com.amarple.expense.read.config.CapitalOneCategoryReader
import com.amarple.expense.read.config.ChaseCategoryReader
import com.amarple.expense.read.config.DescriptionPatternCategoryReader
import com.amarple.expense.read.config.DescriptionPatternExpectedExpenseReader
import com.amarple.expense.read.config.DiscoverCategoryReader
import com.amarple.expense.read.expected.ExpectedExpenseReader
import com.amarple.expense.read.expected.UberSheetExpectedExpenseReader
import com.amarple.expense.read.report.AmExExpenseReportReader
import com.amarple.expense.read.report.BoaExpenseReportReader
import com.amarple.expense.read.report.CapitalOneExpenseReportReader
import com.amarple.expense.read.report.ChaseExpenseReportReader
import com.amarple.expense.read.report.DiscoverExpenseReportReader
import com.amarple.expense.read.report.ExpenseReportReaderSelector
import com.amarple.expense.read.report.WellsFargoExpenseReportReader
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ExpenseApplication {
    @Bean
    fun expectedExpenseReader(): ExpectedExpenseReader = UberSheetExpectedExpenseReader()

    @Bean
    fun amExCategoryReader(): AmExCategoryReader = AmExCategoryReader()

    @Bean
    fun capitalOneCategoryReader(): CapitalOneCategoryReader = CapitalOneCategoryReader()

    @Bean
    fun chaseCategoryReader(): ChaseCategoryReader = ChaseCategoryReader()

    @Bean
    fun discoverCategoryReader(): DiscoverCategoryReader = DiscoverCategoryReader()

    @Bean
    fun patternCategoryReader(): DescriptionPatternCategoryReader = DescriptionPatternCategoryReader()

    @Bean
    fun patternExpectedExpenseReader(): DescriptionPatternExpectedExpenseReader = DescriptionPatternExpectedExpenseReader()

    @Bean
    fun expenseReportReaderSelector(): ExpenseReportReaderSelector = ExpenseReportReaderSelector(
        mapOf(
            ExpenseReportType.Discover to DiscoverExpenseReportReader(),
            ExpenseReportType.BankOfAmerica to BoaExpenseReportReader(),
            ExpenseReportType.WellsFargo to WellsFargoExpenseReportReader(),
            ExpenseReportType.AmericanExpress to AmExExpenseReportReader(),
            ExpenseReportType.CapitalOne to CapitalOneExpenseReportReader(),
            ExpenseReportType.Chase to ChaseExpenseReportReader(),
        )
    )

    @Bean
    fun aggregationSelector(): AggregationSelector = AggregationSelector(
        mapOf<AggregationType?, AggregateBy>(
            AggregationType.Nothing to AggregateNothing,
            AggregationType.Category to AggregateByCategory,
            AggregationType.Instrument to AggregateByInstrument,
            AggregationType.CategoryAndInstrument to AggregateByCategoryAndInstrument,
        ).withDefault { AggregateByCategoryAndInstrument }
    )

    @Bean
    fun importTask(
        expectedExpenseReader: ExpectedExpenseReader,
        amExCategoryReader: AmExCategoryReader,
        capitalOneCategoryReader: CapitalOneCategoryReader,
        chaseCategoryReader: ChaseCategoryReader,
        discoverCategoryReader: DiscoverCategoryReader,
        patternCategoryReader: DescriptionPatternCategoryReader,
        patternExpectedExpenseReader: DescriptionPatternExpectedExpenseReader,
        expenseReportReaderSelector: ExpenseReportReaderSelector,
        aggregationSelector: AggregationSelector,
    ): ImportTask = ImportTask(
        expectedExpenseReader,
        amExCategoryReader,
        capitalOneCategoryReader,
        chaseCategoryReader,
        discoverCategoryReader,
        patternCategoryReader,
        patternExpectedExpenseReader,
        expenseReportReaderSelector,
        aggregationSelector,
    )

    @Bean
    fun importCommandLineRunner(importTask: ImportTask): ImportCommandLineRunner = ImportCommandLineRunner(importTask)
}

fun main(args: Array<String>) {
    runApplication<ExpenseApplication>(*args)
}
