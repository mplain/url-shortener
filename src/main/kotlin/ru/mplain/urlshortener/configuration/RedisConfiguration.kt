package ru.mplain.urlshortener.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations

@Configuration
class RedisConfiguration {

    @Bean
    fun redisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, Any> =
        RedisTemplate<String, Any>().apply { setConnectionFactory(factory) }

    @Bean
    fun redisValueOps(redisTemplate: RedisTemplate<String, Any>): ValueOperations<String, Any> =
        redisTemplate.opsForValue()
}
