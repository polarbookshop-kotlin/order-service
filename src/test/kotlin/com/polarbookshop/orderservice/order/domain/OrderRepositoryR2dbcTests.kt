package com.polarbookshop.orderservice.order.domain

import com.polarbookshop.orderservice.config.DataConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.test.StepVerifier

@DataR2dbcTest
@Import(DataConfig::class)
@Testcontainers
class OrderRepositoryR2dbcTests {

    companion object {
        @Container
        @ServiceConnection
        val postgresql: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:14.4"));

        @DynamicPropertySource
        fun postgresqlProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url"){ r2dbcUrl() }
            registry.add("spring.r2dbc.username"){ postgresql.username }
            registry.add("spring.r2dbc.password"){ postgresql.password }
            registry.add("spring.flyway.url"){ postgresql.jdbcUrl }

        }

        private fun r2dbcUrl() :String {
            return "r2dbc:postgresql://${postgresql.host}:${postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)}/${postgresql.databaseName}"
        }
    }

    @Autowired
    private lateinit var orderRepository: OrderRepository;

    @Test
    fun `creat rejected order`(){
        val rejectedOrder = OrderService.buildRejectionOrder("1234567890", 3);
        StepVerifier
            .create(orderRepository.save(rejectedOrder))
            .expectNextMatches { order -> order.status == OrderStatus.REJECTED }
            .verifyComplete()
    }

}