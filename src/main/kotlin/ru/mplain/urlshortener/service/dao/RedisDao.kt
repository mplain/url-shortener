package ru.mplain.urlshortener.service.dao

import org.springframework.context.annotation.Profile
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.mplain.urlshortener.model.ShortenedUrl
import ru.mplain.urlshortener.repository.ShortenedUrlRepository

@Service
@Profile("redis")
class RedisDao(private val repository: ShortenedUrlRepository) : UrlShortenerDao {

    override fun save(id: String, url: String): String =
        repository.save(ShortenedUrl(id, url)).id

    override fun getById(id: String): String? =
        repository.findByIdOrNull(id)?.url
}
