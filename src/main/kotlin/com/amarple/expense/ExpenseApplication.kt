package com.amarple.expense

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class ExpenseApplication

fun main(args: Array<String>) {
    runApplication<ExpenseApplication>(*args)
}
