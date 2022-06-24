package ru.mplain.urlshortener.service.dao

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.annotation.Profile
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Service
import ru.mplain.urlshortener.configuration.profiles.REDIS

@Service
@Profile(REDIS)
class RedisDao(private val redisTemplate: ReactiveStringRedisTemplate) : UrlShortenerDao {

    private val ops = redisTemplate.opsForValue()

    override suspend fun nextval(): Long =
        ops.incrementAndAwait(SEQUENCE, 1)

    override suspend fun save(id: String, url: String): String {
        val saved = ops.setIfAbsentAndAwait(id, url)
        if (!saved) throw DuplicateKeyException("Duplicate key")
        return id
    }

    override suspend fun getById(id: String): String? =
        ops.getAndAwait(id)

    override suspend fun reset() {
        redisTemplate.connectionFactory.reactiveConnection.serverCommands().flushDb().awaitSingle()
    }
}

private const val SEQUENCE = "shortened_url_sequence"
