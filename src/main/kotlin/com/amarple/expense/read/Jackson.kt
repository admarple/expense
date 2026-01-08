package com.amarple.expense.read

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper

object Jackson {
    val csvMapper by lazy { CsvMapper() }
    val mapper by lazy { ObjectMapper() }
}