package ru.mplain.urlshortener.controller

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.http.HttpStatus.PERMANENT_REDIRECT
import org.springframework.http.HttpStatus.TOO_MANY_REQUESTS
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.mplain.urlshortener.model.ShortenUrlRequest
import ru.mplain.urlshortener.service.UrlShortenerService
import java.net.URI
import java.time.Duration

@RestController
class UrlShortenerController(private val service: UrlShortenerService) {

    private val bucket = Bucket.builder()
        .addLimit(Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1))))
        .build()

    @PostMapping
    fun shorten(@RequestBody request: ShortenUrlRequest): ResponseEntity<Void> = bucketed {
        val id = service.shorten(request.url)
        ResponseEntity.created(URI("/$id")).build()
    }

    @GetMapping("/{id}")
    fun redirect(@PathVariable id: String): ResponseEntity<Void> = bucketed {
        val url = service.getById(id)
        ResponseEntity.status(PERMANENT_REDIRECT).location(URI(url)).build()
    }

    private inline fun <reified T> bucketed(processRequest: () -> ResponseEntity<T>): ResponseEntity<T> =
        if (bucket.tryConsume(1)) {
            processRequest()
        } else {
            ResponseEntity.status(TOO_MANY_REQUESTS).build()
        }
}
