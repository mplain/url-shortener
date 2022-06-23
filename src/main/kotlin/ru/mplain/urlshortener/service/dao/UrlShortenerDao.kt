package ru.mplain.urlshortener.service.dao

interface UrlShortenerDao {

    suspend fun nextval(): Long

    suspend fun save(id: String, url: String): String

    suspend fun getById(id: String): String?

    suspend fun reset()
}
