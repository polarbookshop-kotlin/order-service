package com.polarbookshop.orderservice.order.domain

import com.polarbookshop.orderservice.book.Book
import com.polarbookshop.orderservice.book.BookClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val bookClient: BookClient) {

    fun getAllOrders(): Flux<Order>{
        return orderRepository.findAll()
    }

    fun submitOrder(isbn: String, quantity: Int): Mono<Order>{
        return bookClient.getBookByIsbn(isbn)
            .map { buildAcceptedOrder(it, quantity) }
            .defaultIfEmpty(buildRejectionOrder(isbn, quantity))
            .flatMap { orderRepository.save(it) }
    }

    companion object {
        fun buildRejectionOrder(bookIsbn: String, quantity: Int): Order{
            return Order(bookIsbn, null, null, quantity, OrderStatus.REJECTED)
        }

        fun buildAcceptedOrder(book: Book, quantity: Int): Order{
            return Order(book.isbn, book.title + "-" + book.author, book.price, quantity, OrderStatus.ACCEPTED)
        }
    }


}