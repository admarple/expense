package com.amarple.expense.cli

import com.amarple.expense.model.ImportInput
import com.amarple.expense.model.ImportOutput

class ImportTask {
    fun execute(importInput: ImportInput): ImportOutput {
        // 1. Read the expected expenses using the classes in com.amarple.expense.read.expected
        // 1.a. Read the description patterns, which will be used when matching expected expenses
        // 2. Read the configuration for categories, which will be used when categorizing expenses
        // 3. Read the reports using the classes in com.amarple.expense.read.report
        // 4. Try to match transactions from the reports to expected expenses
        // 5. Try to categorize transactions from the reports
        // 5.a. TODO: configure the logic for categorizing transactions from each report, e.g. DiscoverCategoryCategorizer can only be used for Discover reports
        // 5.b. TODO: find a way to categorize auto-payments so that we can exclude them
        // 5.c. TODO: find a way to categorize incoming deposits and outgoing transfers so that we can return them separately
        // 6. Calculate new AggregatedTransactions
        // 6.a. Group transactions by category and instrument ...
        // 6.b. ... excluding transactions that have already been matched to expected expenses
        // 7. Build the response, including ...
        // 7.a. ... a list of transactions for expected expenses, in the same order as config, with null to designate expenses where no transaction was matched
        // 7.b. ... a list of transactions for categorized expenses
        // 7.c. ... a list of transactions for outgoing transfers
        // 7.d. ... a list of transactions for incoming deposits or rewards
        // 7.e. ... warnings for ...
        // 7.e.1. ... any errors encountered while reading reports
        // 7.e.2. ... any expected expenses that matched multiple transactions
        // 7.e.3. ... any transactions that matched multiple expected expenses
        // 7.f. ... the month for which the report was generated
        TODO("finish implementation")
    }
}