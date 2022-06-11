package ru.mplain.urlshortener.service.dao

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.deleteAndAwait
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.mplain.urlshortener.configuration.profiles.REDIS
import ru.mplain.urlshortener.model.SHORTENED_URL_SEQ
import ru.mplain.urlshortener.model.ShortenedUrl
import ru.mplain.urlshortener.repository.RedisRepository

@Service
@Profile(REDIS)
class RedisDao(
    private val repository: RedisRepository,
    private val redisTemplate: ReactiveStringRedisTemplate
) : UrlShortenerDao {

    private val redisValueOps = redisTemplate.opsForValue()

    override suspend fun incrementSequence(add: Long): Long =
        redisValueOps.increment(SHORTENED_URL_SEQ, add).awaitSingle()

    override suspend fun save(id: String, url: String): String = withContext(Dispatchers.IO) {
        repository.save(ShortenedUrl(id, url)).id
    }

    override suspend fun getById(id: String): String? = withContext(Dispatchers.IO) {
        repository.findByIdOrNull(id)?.url
    }

    override suspend fun reset() {
        withContext(Dispatchers.IO) { repository.deleteAll() }
        redisTemplate.deleteAndAwait(SHORTENED_URL_SEQ)
    }
}
