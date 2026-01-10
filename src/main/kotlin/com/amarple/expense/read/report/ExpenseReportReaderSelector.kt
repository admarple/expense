package com.amarple.expense.read.report

class ExpenseReportReaderSelector(
    private val readers: Map<ExpenseReportType, ExpenseReportReader> = mapOf()
) {
    fun getReader(type: ExpenseReportType): ExpenseReportReader? = readers[type]
}

enum class ExpenseReportType {
    BankOfAmerica,
    Discover,
    WellsFargo,
    AmericanExpress,
    Chase,
    CapitalOne,
}