package com.amarple.expense.read

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object Jackson {
    val csvMapper by lazy {
        CsvMapper().also {
            it.registerKotlinModule()
            it.registerModule(JavaTimeModule())
            it.enable(CsvParser.Feature.EMPTY_STRING_AS_NULL)
            it.enable(CsvParser.Feature.SKIP_EMPTY_LINES)
        }
    }

    val mapper by lazy {
        ObjectMapper().also {
            it.registerKotlinModule()
            it.registerModule(JavaTimeModule())
        }
    }
}