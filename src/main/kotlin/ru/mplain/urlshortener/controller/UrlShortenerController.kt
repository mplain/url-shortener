package ru.mplain.urlshortener.controller

import org.springframework.http.HttpStatus.PERMANENT_REDIRECT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.mplain.urlshortener.model.ShortenUrlRequest
import ru.mplain.urlshortener.service.UrlShortenerService
import java.net.URI

@RestController
class UrlShortenerController(private val urlShortenerService: UrlShortenerService) {

    @PostMapping
    suspend fun shorten(@RequestBody request: ShortenUrlRequest): ResponseEntity<Void> {
        val id = urlShortenerService.shorten(request.url)
        return ResponseEntity.created(URI("/$id")).build()
    }

    @GetMapping("/{id}")
    suspend fun redirect(@PathVariable id: String): ResponseEntity<Void> {
        val url = urlShortenerService.getById(id)
        return ResponseEntity.status(PERMANENT_REDIRECT).location(URI(url)).build()
    }
}
