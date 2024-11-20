package com.polarbookshop.orderservice.order.domain

import jakarta.validation.constraints.Pattern
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("orders")
data class Order (
    @Id
    val id: Long?,

    @field:Pattern(
        regexp = "^([0-9]{10}|[0-9]{13})$",
        message = "Invalid ISBN. Must have 10 or 13 digits"
    )
    val bookIsbn: String,
    val bookName: String?,
    val bookPrice: Double?,
    val quantity: Int,
    val status: OrderStatus,

    @CreatedDate
    val createdDate: Instant?,

    @LastModifiedDate
    val lastModifiedDate: Instant?,

    @Version
    val version: Int
){
    constructor(
        bookIsbn: String,
        bookName: String?,
        bookPrice: Double?,
        quantity: Int,
        status: OrderStatus
    ):this(
        null,
        bookIsbn,
        bookName,
        bookPrice,
        quantity,
        status,
        null,
        null,
        0
    )
}
