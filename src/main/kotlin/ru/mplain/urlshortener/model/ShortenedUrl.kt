package ru.mplain.urlshortener.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class ShortenedUrl(@Id val id: String, val url: String)
