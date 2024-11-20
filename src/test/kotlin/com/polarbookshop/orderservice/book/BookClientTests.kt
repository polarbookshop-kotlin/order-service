package com.polarbookshop.orderservice.book


import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.test.StepVerifier

class BookClientTests {
    private lateinit var mockWebServer: MockWebServer;
    private lateinit var bookClient: BookClient;

    @BeforeEach
    fun setup() {
        this.mockWebServer = MockWebServer();
        this.mockWebServer.start();
        var webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toUri().toString())
            .build();
        this.bookClient = BookClient(webClient);
    }

    @AfterEach
    fun clean() {
        this.mockWebServer.shutdown();
    }

    @Test
    fun `when book exists then return book`(){
        var bookIsbn = "1234567890";
        var mockResponse = MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody("""
                {
                    "isbn": ${bookIsbn},
                    "title": "Title",
                    "author": "Author",
                    "price": 9.90,
                    "publisher": "PolarSophia"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse);

        val book = bookClient.getBookByIsbn(isbn = bookIsbn);

        StepVerifier.create(book)
            .expectNextMatches {
                b ->
                b.isbn == bookIsbn
            }.verifyComplete()

    }
}