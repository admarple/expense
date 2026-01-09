package com.amarple.expense.read

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class JacksonTest {
    @Test
    fun `should provide mappers`() {
        assertNotNull(Jackson.csvMapper)
        assertNotNull(Jackson.mapper)
    }
}
