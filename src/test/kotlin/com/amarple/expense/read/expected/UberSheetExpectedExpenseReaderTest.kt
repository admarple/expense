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

        assertEquals(8, result.size)

        result[0].let {
            assertEquals("Alex's Bank of America (less fixed)", it.name)
            assertEquals(-123.45, it.expectedTransaction.amount)
            assertEquals(Category("Entertainment", "Miscellaneous"), it.expectedTransaction.category)
            assertEquals("Alex's Bank of America", it.expectedTransaction.instrument?.name)
        }

        result[5].let {
            assertEquals("Verizon (Internet)", it.name)
            assertEquals(-39.99, it.expectedTransaction.amount)
            assertEquals(Category("Utilities", "Miscellaneous"), it.expectedTransaction.category)
            assertEquals("Joint Wells Fargo", it.expectedTransaction.instrument?.name)
        }

        result[7].let {
            assertEquals("Apple Cloud Storage", it.name)
            assertEquals(-2.99, it.expectedTransaction.amount)
            assertEquals(Category("Utilities", "Miscellaneous"), it.expectedTransaction.category)
            assertEquals("Alex's AmEx", it.expectedTransaction.instrument?.name)
        }
    }
}
