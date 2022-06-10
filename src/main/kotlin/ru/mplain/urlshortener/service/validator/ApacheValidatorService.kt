package ru.mplain.urlshortener.service.validator

import org.apache.commons.validator.routines.UrlValidator
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("apache")
class ApacheValidatorService : ValidatorService {

    private val urlValidator = UrlValidator()

    override fun validate(url: String): Boolean = urlValidator.isValid(url)
}
