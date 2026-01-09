package com.amarple.expense.read.expected

import com.amarple.expense.model.ExpectedExpensesInput
import com.amarple.expense.model.DescriptionPatternExpectedExpensesInput
import com.amarple.expense.model.internal.Category
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UberSheetExpectedExpenseReaderTest {

    @Test
    fun `should read expected expenses from UberSheet CSV`() {
        val csvPath = this::class.java.getResource("/expected_expenses.csv")!!.path
        val reader = UberSheetExpectedExpenseReader()

        val result = reader.read(ExpectedExpensesInput(csvPath, DescriptionPatternExpectedExpensesInput("")))

        assertEquals(6, result.size)

        assertEquals("Alex's Bank of America (less fixed)", result[0].name)
        assertEquals(123.45, result[0].expectedTransaction.amount)
        assertEquals(Category("Entertainment", "Miscellaneous"), result[0].expectedTransaction.category)
        assertEquals("Alex's Bank of America", result[0].expectedTransaction.instrument?.name)

        assertEquals("Verizon (Internet)", result[5].name)
        assertEquals(39.99, result[5].expectedTransaction.amount)
        assertEquals(Category("Utilities", "Miscellaneous"), result[5].expectedTransaction.category)
        assertEquals("Joint Wells Fargo", result[5].expectedTransaction.instrument?.name)
    }
}
