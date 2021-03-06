package ru.mplain.urlshortener.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class ShortenedUrlSequence(@Id val id: String, val seq: Long)
