package ru.mplain.urlshortener.repository

import org.springframework.context.annotation.Profile
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.mplain.urlshortener.configuration.profiles.REDIS
import ru.mplain.urlshortener.model.ShortenedUrl

@Repository
@Profile(REDIS)
interface RedisRepository : CrudRepository<ShortenedUrl, String>
