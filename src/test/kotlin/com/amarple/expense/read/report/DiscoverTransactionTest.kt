package com.amarple.expense.read.report

import com.amarple.expense.model.internal.PaymentInstrument
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DiscoverTransactionTest {

    @Test
    fun `should create DiscoverTransaction from DiscoverExpenseLine`() {
        val line = DiscoverExpenseLine(
            transactionDate = LocalDate.of(2023,1,1),
            localDate = LocalDate.of(2023,1,2),
            description = "AMAZON",
            amount = 10.0,
            discoverCategoryName = "Merchandise"
        )

        val transaction = DiscoverTransaction(line, "MyCard")

        assertEquals(LocalDate.of(2023, 1, 1), transaction.transactionDate)
        assertEquals(LocalDate.of(2023, 1, 2), transaction.postDate)
        assertEquals(-10.0, transaction.amount)
        assertEquals("AMAZON", transaction.description)
        assertEquals(PaymentInstrument("MyCard"), transaction.instrument)
        assertEquals(DiscoverCategory.MERCHANDISE, transaction.discoverCategory)
    }
}
