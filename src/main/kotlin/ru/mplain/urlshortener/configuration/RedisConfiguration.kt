package ru.mplain.urlshortener.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import ru.mplain.urlshortener.configuration.profiles.REDIS
import ru.mplain.urlshortener.model.ShortenedUrl

@Configuration
@Profile(REDIS)
class RedisConfiguration {

    @Bean
    fun shortenedUrlSerializationContext(objectMapper: ObjectMapper): RedisSerializationContext<String, ShortenedUrl> =
        RedisSerializationContext.newSerializationContext<String, ShortenedUrl>(RedisSerializer.string())
            .hashValue(Jackson2JsonRedisSerializer(ShortenedUrl::class.java).apply { setObjectMapper(objectMapper) })
            .build()
}
