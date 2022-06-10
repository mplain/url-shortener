package ru.mplain.urlshortener.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.mplain.urlshortener.model.ShortenedUrl

@Repository
interface ShortenedUrlRepository : CrudRepository<ShortenedUrl, Long>
