package ru.mplain.urlshortener.controller

import org.springframework.http.HttpStatus.PERMANENT_REDIRECT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import ru.mplain.urlshortener.model.ShortenUrlRequest
import ru.mplain.urlshortener.service.UrlShortenerService
import java.net.URI

@RestController
class UrlShortenerController(private val service: UrlShortenerService) {

    @PostMapping
    fun shorten(@RequestBody request: ShortenUrlRequest, uriBuilder: UriComponentsBuilder): ResponseEntity<Void> {
        val idBase62 = service.shorten(request.url)
        val uri = uriBuilder.path("/{id}").buildAndExpand(idBase62).toUri()
        return ResponseEntity.created(uri).build()
    }

    @GetMapping("/{id}")
    fun redirect(@PathVariable id: String): ResponseEntity<Void> {
        val url = service.getById(id)
        return ResponseEntity.status(PERMANENT_REDIRECT).location(URI(url)).build()
    }
}
