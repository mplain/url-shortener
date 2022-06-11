package ru.mplain.urlshortener.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.redis.core.RedisHash

@Document
@RedisHash(timeToLive = REDIS_TIME_TO_LIVE)
data class ShortenedUrl(@Id val id: String, val url: String)

const val SHORTENED_URL = "shortened_url"
const val SHORTENED_URL_SEQ = "shortened_url_seq"

private const val REDIS_TIME_TO_LIVE = 30 * 24 * 60 * 60L
