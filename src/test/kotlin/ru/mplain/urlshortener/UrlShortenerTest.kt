package ru.mplain.urlshortener

import com.ninjasquad.springmockk.SpykBean
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.lettuce.core.RedisException
import io.mockk.clearAllMocks
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ValueOperations
import org.springframework.test.web.reactive.server.ExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import ru.mplain.urlshortener.model.ShortenUrlRequest
import ru.mplain.urlshortener.repository.ShortenedUrlRepository

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class UrlShortenerTest(
    private val webTestClient: WebTestClient,
    private val repository: ShortenedUrlRepository
) : FeatureSpec() {

    @SpykBean
    lateinit var redisValueOps: ValueOperations<String, Any>

    override fun extensions(): List<Extension> = listOf(SpringExtension)

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        repository.deleteAll()
        clearAllMocks()
    }

    init {
        feature("shorten url") {
            scenario("success") {
                shortenUrl(EXAMPLE_URL)
                    .expectStatus().isCreated
                    .expectBody().isEmpty
                    .shortenedUri()
                    .shouldBe("MQ==")
            }
            scenario("value exists") {
                shortenUrl(EXAMPLE_URL)

                shortenUrl(EXAMPLE_URL)
                    .expectStatus().isCreated
                    .expectBody().isEmpty
                    .shortenedUri()
                    .shouldBe("Mw==")
            }
            scenario("invalid url") {
                shortenUrl("https://www.google.com/ qwerty")
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("message")
                    .isEqualTo("Invalid url")
            }
            scenario("network error") {
                every { redisValueOps.increment(any()) } throws RedisException("Network error")
                shortenUrl(EXAMPLE_URL)
                    .expectStatus().is5xxServerError
                    .expectBody()
                    .jsonPath("message")
                    .isEqualTo("Network error")
            }
        }
        feature("redirect") {
            scenario("success") {
                val shortenedUri = shortenUrl(EXAMPLE_URL)
                    .expectStatus().isCreated
                    .expectBody().isEmpty
                    .shortenedUri()

                redirect(shortenedUri)
                    .expectStatus().isPermanentRedirect
                    .expectBody().isEmpty
                    .responseHeaders
                    .location?.toString()
                    .shouldBe(EXAMPLE_URL)
            }
            scenario("not found") {
                redirect("abc")
                    .expectStatus().isNotFound
                    .expectBody()
                    .jsonPath("message")
                    .isEqualTo("Not Found")
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

    private fun ExchangeResult.shortenedUri() =
        this
            .responseHeaders
            .location
            .shouldNotBeNull()
            .path
            .substringAfter("/")
}

private const val EXAMPLE_URL = "https://www.google.com/qwerty"
