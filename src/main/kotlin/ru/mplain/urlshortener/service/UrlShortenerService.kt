package ru.mplain.urlshortener.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import ru.mplain.urlshortener.configuration.SHORTENED_URL
import ru.mplain.urlshortener.service.dao.UrlShortenerDao
import ru.mplain.urlshortener.service.encoder.EncoderService
import ru.mplain.urlshortener.service.validator.ValidatorService

@Service
class UrlShortenerService(
    private val validatorService: ValidatorService,
    private val encoderService: EncoderService,
    private val dao: UrlShortenerDao
) {

    suspend fun shorten(url: String): String {
        val validated = validatorService.validate(url)
        if (!validated) throw ResponseStatusException(BAD_REQUEST, "Invalid url")
        return save(url)
    }

    private suspend fun save(url: String, retries: Int = DEFAULT_RETRIES): String =
        try {
            val id = dao.nextval()
            val encoded = encoderService.encode(id)
            dao.save(encoded, url)
        } catch (e: DuplicateKeyException) {
            if (retries > 0) save(url, retries - 1) else throw DuplicateKeyException("Duplicate key")
        }

    @Cacheable(SHORTENED_URL)
    suspend fun getById(id: String): String =
        dao.getById(id) ?: throw ResponseStatusException(NOT_FOUND, "Not found")
}

private const val DEFAULT_RETRIES = 2
