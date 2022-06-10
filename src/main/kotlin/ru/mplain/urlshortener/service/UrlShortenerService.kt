package ru.mplain.urlshortener.service

import org.springframework.stereotype.Service
import ru.mplain.urlshortener.model.ShortenedUrl
import ru.mplain.urlshortener.repository.ShortenedUrlRepository
import java.net.URL
import java.util.*

@Service
class UrlShortenerService(private val repository: ShortenedUrlRepository) {

    fun shorten(url: String): String {
        URL(url) // validation
        val entity = ShortenedUrl(url = url)
        val saved = repository.save(entity)
        val id = requireNotNull(saved.id)
        return Base64.getEncoder().encodeToString(id.toString().toByteArray())
    }

    fun getById(id: String): String {
        val decoded = Base64.getDecoder().decode(id).let(::String).toLong()
        val result = repository.findById(decoded)
        return result.get().url
    }
}
