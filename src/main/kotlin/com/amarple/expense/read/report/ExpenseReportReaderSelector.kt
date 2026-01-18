package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportType

class ExpenseReportReaderSelector(
    private val readers: Map<ExpenseReportType, ExpenseReportReader> = mapOf()
) {
    fun getReader(type: ExpenseReportType): ExpenseReportReader? = readers[type]
}