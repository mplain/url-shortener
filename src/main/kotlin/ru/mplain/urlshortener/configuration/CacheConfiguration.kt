package ru.mplain.urlshortener.configuration

import com.google.common.cache.CacheBuilder
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.cache.support.NoOpCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import ru.mplain.urlshortener.configuration.profiles.PROD
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfiguration {

    @Bean
    @Primary
    @Profile(PROD)
    fun prodCacheManager(): CacheManager = object : ConcurrentMapCacheManager(SHORTENED_URL) {
        override fun createConcurrentMapCache(name: String): Cache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .maximumSize(MAXIMUM_CACHE_SIZE)
            .build<Any, Any>()
            .asMap()
            .let { ConcurrentMapCache(name, it, false) }
    }

    @Bean
    @Primary
    @Profile("!$PROD")
    fun testCacheManager(): CacheManager = NoOpCacheManager()
}

const val SHORTENED_URL = "shortened_url"
private const val MAXIMUM_CACHE_SIZE = 1000L
