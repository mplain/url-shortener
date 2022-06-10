package ru.mplain.urlshortener.service.sequence

import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service

@Service
@Profile("redis")
class RedisSequenceService(private val redisValueOps: ValueOperations<String, Any>) : SequenceService {

    override fun nextval(): Long = redisValueOps.increment("id")!!
}
