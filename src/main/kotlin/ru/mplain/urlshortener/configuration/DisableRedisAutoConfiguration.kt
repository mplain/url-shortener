package ru.mplain.urlshortener.configuration

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import ru.mplain.urlshortener.configuration.profiles.REDIS

@Configuration
@Profile("!$REDIS")
@EnableAutoConfiguration(exclude = [RedisAutoConfiguration::class])
class DisableRedisAutoConfiguration
