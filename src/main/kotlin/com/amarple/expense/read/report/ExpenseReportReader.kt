package com.amarple.expense.read.report

import com.amarple.expense.model.ExpenseReportInput
import com.amarple.expense.model.internal.ExpenseReport

interface ExpenseReportReader {
    fun read(input: ExpenseReportInput): ExpenseReport
}