package com.polarbookshop.orderservice.order.domain

import com.polarbookshop.orderservice.book.Book
import com.polarbookshop.orderservice.book.BookClient
import com.polarbookshop.orderservice.event.OrderAcceptedMessage
import com.polarbookshop.orderservice.event.OrderDispatchedMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val bookClient: BookClient,
    private val streamBridge: StreamBridge) {

    fun getAllOrders(): Flux<Order>{
        return orderRepository.findAll()
    }

    @Transactional
    fun submitOrder(isbn: String, quantity: Int): Mono<Order>{
        return bookClient.getBookByIsbn(isbn)
            .map { buildAcceptedOrder(it, quantity) }
            .defaultIfEmpty(buildRejectionOrder(isbn, quantity))
            .flatMap { orderRepository.save(it) }
            .doOnNext {publishOrderAcceptedEvent(it)}
    }

    private fun publishOrderAcceptedEvent(order: Order) {
        if(order.status!=OrderStatus.ACCEPTED){
            return
        }
        val orderAcceptedMessage = OrderAcceptedMessage(order.id!!)
        log.info("Sending order accepted event with id: ${order.id}")
        val result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage)
        log.info("Result of sending data for order with id: ${order.id}: $result")

    }

    fun consumerOrderDispatchedEvent(flux: Flux<OrderDispatchedMessage>): Flux<Order> {
        return flux.flatMap { orderRepository.findById(it.orderId) }
                .map { buildDispatchedOrder(it) }
                .flatMap { orderRepository.save(it) }

    }

    private fun buildDispatchedOrder(existingOrder: Order) : Order {
        return Order(
            existingOrder.id,
            existingOrder.bookIsbn,
            existingOrder.bookName,
            existingOrder.bookPrice,
            existingOrder.quantity,
            OrderStatus.DISPATCHED,
            existingOrder.createdDate,
            existingOrder.lastModifiedDate,
            existingOrder.version
        )
    }

    companion object {
        fun buildRejectionOrder(bookIsbn: String, quantity: Int): Order{
            return Order(bookIsbn, null, null, quantity, OrderStatus.REJECTED)
        }

        fun buildAcceptedOrder(book: Book, quantity: Int): Order{
            return Order(book.isbn, book.title + "-" + book.author, book.price, quantity, OrderStatus.ACCEPTED)
        }

        val log: Logger = LoggerFactory.getLogger(OrderService::class.java)
    }


}