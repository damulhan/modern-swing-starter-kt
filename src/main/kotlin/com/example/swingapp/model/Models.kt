package com.example.swingapp.model

import java.time.LocalDate

data class Customer(
    val id: Long? = null,
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var isActive: Boolean = true
)

data class Order(
    val id: String,
    val customerName: String,
    val status: String,
    val amount: Double,
    val date: LocalDate
)
