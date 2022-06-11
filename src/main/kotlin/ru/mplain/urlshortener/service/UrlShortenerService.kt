package ru.mplain.urlshortener.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException
import ru.mplain.urlshortener.model.SHORTENED_URL
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
        if (!validatorService.validate(url)) throw ServerWebInputException("Invalid url")
        val id = dao.nextval()
        val encoded = encoderService.encode(id)
        return dao.save(encoded, url)
    }

    @Cacheable(SHORTENED_URL)
    suspend fun getById(id: String): String =
        dao.getById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found")
}
