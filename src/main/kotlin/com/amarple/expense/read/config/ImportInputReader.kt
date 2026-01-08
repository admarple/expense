package com.amarple.expense.read.config

import com.amarple.expense.model.ImportInput
import com.amarple.expense.read.Jackson.mapper
import java.io.File

class ImportInputReader {
    fun read(inputPath: String): ImportInput {
        return mapper.readValue(File(inputPath), ImportInput::class.java)
    }
}