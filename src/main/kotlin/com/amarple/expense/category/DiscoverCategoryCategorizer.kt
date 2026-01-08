package com.amarple.expense.category

import com.amarple.expense.model.internal.Category
import com.amarple.expense.read.report.DiscoverCategory
import com.amarple.expense.read.report.DiscoverTransaction

class DiscoverCategoryCategorizer: TransactionCategorizer<DiscoverTransaction> {
    override fun categorize(transaction: DiscoverTransaction): Category? {
        return when (transaction.discoverCategory) {
            DiscoverCategory.MERCHANDISE -> Category("Shopping", "Miscellaneous")
            DiscoverCategory.UNRECOGNIZED -> Category("Entertainment", "Miscellaneous")
            DiscoverCategory.SERVICES -> Category("Utilities", "Miscellaneous")
            DiscoverCategory.PAYMENTS_AND_CREDITS -> Category("Financial_Services", "Payment & Credits")
            DiscoverCategory.TRAVEL_AND_ENTERTAINMENT -> Category("Travel", "Miscellaneous")
            DiscoverCategory.RESTAURANTS -> Category("Entertainment", "Restaurants & Bars")
            DiscoverCategory.SUPERMARKETS -> Category("Grocery", "Miscellaneous")
            DiscoverCategory.GOVERNMENT_SERVICES -> Category("Financial_Services", "Fines & Fees")
            DiscoverCategory.AWARDS_AND_REBATE_CREDITS -> Category("Financial_Services", "Rewards")
        }
    }
}