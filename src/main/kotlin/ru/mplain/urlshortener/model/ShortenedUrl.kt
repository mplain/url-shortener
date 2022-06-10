package ru.mplain.urlshortener.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("shortened_url", timeToLive = TTL)
data class ShortenedUrl(@Id val id: String, val url: String)

private const val TTL = 24 * 60 * 60L
