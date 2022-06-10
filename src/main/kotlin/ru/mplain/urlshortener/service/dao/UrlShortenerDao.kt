package ru.mplain.urlshortener.service.dao

interface UrlShortenerDao {

    fun save(id: String, url: String): String

    fun getById(id: String): String?
}
