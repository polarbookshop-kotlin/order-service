package com.polarbookshop.orderservice.order.web

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class OrderRequest(
    @field:Pattern(
        regexp = "^([0-9]{10}|[0-9]{13})$",
        message = "Invalid ISBN. Must have 10 or 13 digits"
    )
    val isbn: String,

    @field:NotNull(message = "The book quantity must be defined.")
    @field:Min(value = 1, message = "You must order at least 1 item.")
    @field:Max(value = 5, message = "You cannot order more than 5 items.")
    val quantity: Int
)
