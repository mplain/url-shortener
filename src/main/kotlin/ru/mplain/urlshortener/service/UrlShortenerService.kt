package ru.mplain.urlshortener.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException
import ru.mplain.urlshortener.configuration.SHORTENED_URL
import ru.mplain.urlshortener.service.dao.UrlShortenerDao
import ru.mplain.urlshortener.service.encoder.EncoderService
import ru.mplain.urlshortener.service.sequence.SequenceService
import ru.mplain.urlshortener.service.validator.ValidatorService

@Service
class UrlShortenerService(
    private val validatorService: ValidatorService,
    private val sequenceService: SequenceService,
    private val encoderService: EncoderService,
    private val dao: UrlShortenerDao
) {

    fun shorten(url: String): String {
        if (!validatorService.validate(url)) throw ServerWebInputException("Invalid url")
        val id = sequenceService.nextval()
        val encoded = encoderService.encode(id)
        return dao.save(encoded, url)
    }

    @Cacheable(SHORTENED_URL)
    fun getById(id: String): String =
        dao.getById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found")
}
