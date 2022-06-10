package ru.mplain.urlshortener.service

import org.apache.commons.validator.routines.UrlValidator
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException
import ru.mplain.urlshortener.model.ShortenedUrl
import ru.mplain.urlshortener.repository.ShortenedUrlRepository
import java.util.*

@Service
class UrlShortenerService(
    private val repository: ShortenedUrlRepository,
    private val redisValueOps: ValueOperations<String, Any>
) {

    fun shorten(url: String): String {
        if (!urlValidator.isValid(url)) throw ServerWebInputException("Invalid url")
        val id = redisValueOps.increment("id")!!
        val encoded = Base64.getEncoder().encodeToString(id.toString().toByteArray())
        repository.save(ShortenedUrl(encoded, url))
        return encoded
    }

    fun getById(id: String): String {
        val result = repository.findByIdOrNull(id)
        return result?.url ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found")
    }
}

private val urlValidator = UrlValidator()
