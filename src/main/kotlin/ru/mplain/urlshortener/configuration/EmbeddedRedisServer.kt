package ru.mplain.urlshortener.configuration

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
@Profile("!prod")
class EmbeddedRedisServer(redisProperties: RedisProperties) {

    private val redisServer = RedisServer(redisProperties.port)

    @PostConstruct
    fun postConstruct() {
        redisServer.start()
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
    }
}
