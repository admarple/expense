package com.amarple.expense.model.internal

data class Category(
    val category: String,
    val subcategory: String
)

val PAYMENTS = Category("Financial_Services", "Payments")
val REWARDS = Category("Financial_Services", "Rewards")
val TRANSFERS = Category("Financial_Services", "Transfers")
