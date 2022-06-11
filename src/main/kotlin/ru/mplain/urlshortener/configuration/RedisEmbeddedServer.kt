package ru.mplain.urlshortener.configuration

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer
import ru.mplain.urlshortener.configuration.profiles.PROD
import ru.mplain.urlshortener.configuration.profiles.REDIS
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
@Profile("$REDIS & !$PROD")
class RedisEmbeddedServer(redisProperties: RedisProperties) {

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
