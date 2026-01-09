package com.amarple.expense.expected

import com.amarple.expense.model.internal.BasicTransaction
import com.amarple.expense.model.internal.ExpectedExpense
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DescriptionPatternExpectedExpenseMatcherTest {

    @Test
    fun `should match transaction based on description pattern and expense name`() {
        val patterns = listOf(
            DescriptionPatternExpectedExpense(".*RENT.*", "Rent"),
            DescriptionPatternExpectedExpense(".*NETFLIX.*", "Subscriptions")
        )

        val matcher = DescriptionPatternExpectedExpenseMatcher<BasicTransaction>(patterns)

        val tRent = BasicTransaction(LocalDate.now(), -1000.0, "APARTMENT RENT", null, null)
        val tNetflix = BasicTransaction(LocalDate.now(), -15.0, "NETFLIX.COM", null, null)
        val tOther = BasicTransaction(LocalDate.now(), -50.0, "OTHER", null, null)

        val expectedRent = ExpectedExpense("Rent", tRent)
        val expectedSubs = ExpectedExpense("Subscriptions", tNetflix)

        assertTrue(matcher.isMatch(expectedRent, tRent))
        assertTrue(matcher.isMatch(expectedSubs, tNetflix))

        assertFalse(matcher.isMatch(expectedRent, tNetflix))
        assertFalse(matcher.isMatch(expectedRent, tOther))
    }
}
