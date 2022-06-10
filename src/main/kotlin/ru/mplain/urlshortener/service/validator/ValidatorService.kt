package ru.mplain.urlshortener.service.validator

interface ValidatorService {

    fun validate(url: String): Boolean
}
