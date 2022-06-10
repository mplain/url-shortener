package ru.mplain.urlshortener.service.encoder

interface EncoderService {

    fun encode(long: Long): String

    fun decode(string: String): Long
}
