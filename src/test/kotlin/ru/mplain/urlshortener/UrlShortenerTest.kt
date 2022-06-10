package ru.mplain.urlshortener

import com.ninjasquad.springmockk.SpykBean
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.lettuce.core.RedisException
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.clearAllMocks
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import ru.mplain.urlshortener.model.ShortenUrlRequest
import ru.mplain.urlshortener.repository.ShortenedUrlRepository

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class UrlShortenerTest(private val webTestClient: WebTestClient) : FeatureSpec() {

    @SpykBean
    lateinit var repository: ShortenedUrlRepository

    override fun extensions(): List<Extension> = listOf(SpringExtension)

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        repository.deleteAll()
        clearAllMocks()
    }

    init {
        feature("shorten url") {
            scenario("success") {
                shortenUrl("https://www.google.com/qwerty")
                    .expectStatus().isCreated
                    .expectBody().isEmpty
                    .responseHeaders
                    .location
                    .shouldNotBeNull()
                    .path
                    .shouldBe("/abc")
            }
            scenario("value exists") {
                shortenUrl("https://www.google.com/qwerty")

                shortenUrl("https://www.google.com/qwerty")
                    .expectStatus().isCreated
                    .expectBody().isEmpty
                    .responseHeaders
                    .location
                    .shouldNotBeNull()
                    .toString()
                    .shouldBe("/abc")
            }
            scenario("invalid url") {
                shortenUrl("https://www.google.com/ qwerty")
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("message")
                    .isEqualTo("invalid url")
            }
            scenario("network error") {
                every { repository.save(any()) } throws RedisException("Network error")
                shortenUrl("https://www.google.com/qwerty")
                    .expectStatus().is5xxServerError
                    .expectBody()
                    .jsonPath("message")
                    .isEqualTo("Network error")
            }
        }
        feature("redirect") {
            scenario("success") {
                val shortenedUrl = shortenUrl("https://www.google.com/qwerty")
                    .expectStatus().isCreated
                    .expectBody().isEmpty
                    .responseHeaders
                    .location
                    .shouldNotBeNull()
                    .toString()

                redirect(shortenedUrl)
                    .expectStatus().isNotFound
                    .expectBody()
                    .toStr()
                    .shouldContain("not found on this server")
            }
            scenario("not found") {
                redirect("/abc")
                    .expectStatus().isNotFound
                    .expectBody()
                    .toStr()
                    .shouldContain("not found on this server")
            }
        }
    }

    private fun shortenUrl(url: String): WebTestClient.ResponseSpec =
        webTestClient
            .post()
            .uri("/")
            .bodyValue(ShortenUrlRequest(url))
            .exchange()

    private fun redirect(id: String): WebTestClient.ResponseSpec =
        webTestClient
            .get()
            .uri("/$id")
            .exchange()
}
