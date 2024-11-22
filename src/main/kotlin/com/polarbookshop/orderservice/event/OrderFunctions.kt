package com.polarbookshop.orderservice.event

import com.polarbookshop.orderservice.order.domain.OrderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.util.function.Consumer

@Configuration
class OrderFunctions {
    companion object {
        val log: Logger = LoggerFactory.getLogger(OrderFunctions::class.java)
    }

    @Bean
    fun dispatchOrder(orderService: OrderService) : (Flux<OrderDispatchedMessage>) -> Unit {
        return {
            orderService.consumerOrderDispatchedEvent(it)
                .doOnNext { order ->
                    log.info("The order with id {} is dispatched", order.id)
                }
                .subscribe()
        }
    }
}