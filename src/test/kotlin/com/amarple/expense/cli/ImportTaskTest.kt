package com.amarple.expense.cli

import com.amarple.expense.model.ImportInput
import com.amarple.expense.model.ExpectedExpensesInput
import com.amarple.expense.model.DescriptionPatternExpectedExpensesInput
import com.amarple.expense.model.CategoriesInput
import com.amarple.expense.model.DescriptionPatternCategoryInput
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ImportTaskTest {

    @Test
    fun `execute should throw NotImplementedError`() {
        val task = ImportTask()
        val input = ImportInput(
            reports = emptyList(),
            expectedExpenses = ExpectedExpensesInput("", DescriptionPatternExpectedExpensesInput("")),
            categories = CategoriesInput("", DescriptionPatternCategoryInput(""))
        )

        assertThrows<NotImplementedError> {
            task.execute(input)
        }
    }
}
