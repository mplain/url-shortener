package ru.mplain.urlshortener.service.dao

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Service
import ru.mplain.urlshortener.configuration.profiles.REDIS
import ru.mplain.urlshortener.model.ShortenedUrl

@Service
@Profile(REDIS)
class RedisDao(
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val objectMapper: ObjectMapper
) : UrlShortenerDao {

    private val valueOps = redisTemplate.opsForValue()
    private val hashOps = redisTemplate.opsForHash<String, String>()

    override suspend fun nextval(): Long =
        valueOps.incrementAndAwait(SEQUENCE, 1)

    override suspend fun save(id: String, url: String): String {
        val key = "$KEYSPACE:$id"
        val keyExists = redisTemplate.hasKeyAndAwait(key)
        if (keyExists) throw DuplicateKeyException("Duplicate key")
        val pojo = ShortenedUrl(id, url)
        val map = objectMapper.convertValue<Map<String, String>>(pojo)
        val saved = hashOps.putAllAndAwait(key, map)
        if (!saved) throw IllegalStateException("Value not saved")
        return id
    }

    override suspend fun getById(id: String): String? =
        hashOps.getAndAwait("$KEYSPACE:$id", ShortenedUrl::url.name)

    override suspend fun reset() {
        val keys = redisTemplate.keysAsFlow("$KEYSPACE:*").toList()
        if (keys.isNotEmpty()) redisTemplate.deleteAndAwait(*keys.toTypedArray())
        redisTemplate.deleteAndAwait(KEYSPACE, SEQUENCE)
    }
}

private const val KEYSPACE = "shortened_url"
private const val SEQUENCE = "shortened_url_sequence"
