package ru.mplain.urlshortener.service.dao

import org.springframework.context.annotation.Profile
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.redis.core.*
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.stereotype.Service
import ru.mplain.urlshortener.configuration.profiles.REDIS
import ru.mplain.urlshortener.model.ShortenedUrl

@Service
@Profile(REDIS)
class RedisDao(
    private val redisTemplate: ReactiveStringRedisTemplate,
    serializationContext: RedisSerializationContext<String, ShortenedUrl>
) : UrlShortenerDao {

    private val sequenceOps = redisTemplate.opsForValue()
    private val shortenedUrlOps = redisTemplate.opsForHash<String, String, ShortenedUrl>(serializationContext)

    override suspend fun nextval(): Long =
        sequenceOps.incrementAndAwait(SEQUENCE, 1)

    override suspend fun save(id: String, url: String): String {
        val saved = shortenedUrlOps.putIfAbsentAndAwait(HASH_KEY, id, ShortenedUrl(id, url))
        if (!saved) throw DuplicateKeyException("Duplicate key")
        return id
    }

    override suspend fun getById(id: String): String? =
        shortenedUrlOps.getAndAwait(HASH_KEY, id)?.url

    override suspend fun reset() {
        redisTemplate.deleteAndAwait(HASH_KEY, SEQUENCE)
    }
}

private const val HASH_KEY = "shortened_url"
private const val SEQUENCE = "shortened_url_sequence"
