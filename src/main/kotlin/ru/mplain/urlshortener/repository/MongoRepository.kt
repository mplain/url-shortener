package ru.mplain.urlshortener.repository

import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import ru.mplain.urlshortener.configuration.profiles.MONGO
import ru.mplain.urlshortener.model.ShortenedUrl

@Repository
@Profile(MONGO)
interface MongoRepository : ReactiveMongoRepository<ShortenedUrl, String>
