package com.polarbookshop.orderservice.order.web

import com.polarbookshop.orderservice.order.domain.Order
import com.polarbookshop.orderservice.order.domain.OrderService
import com.polarbookshop.orderservice.order.domain.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


@WebFluxTest
class OrderControllerWebFluxTests {
    @Autowired
    private lateinit var webClient: WebTestClient

    @MockBean
    private lateinit var orderService: OrderService

    @Test
    fun `when book not available then reject order`() {
        var orderRequest = OrderRequest("1234567890", 3)
        var expectedOrder = OrderService.buildRejectionOrder(orderRequest.isbn, orderRequest.quantity)
        given(
            orderService.submitOrder(
                orderRequest.isbn, orderRequest.quantity
            )
        ).willReturn(Mono.just(expectedOrder))

        webClient
            .post()
            .uri("/orders")
            .bodyValue(orderRequest)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(Order::class.java).value {
                assertThat(it).isNotNull
                assertThat(it.status).isEqualTo(OrderStatus.REJECTED)
            }
    }
}