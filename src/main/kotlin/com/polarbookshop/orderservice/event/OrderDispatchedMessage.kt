package com.polarbookshop.orderservice.event

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class OrderDispatchedMessage @JsonCreator constructor(
    @JsonProperty("orderId") val orderId: Long
)