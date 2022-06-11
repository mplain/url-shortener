package ru.mplain.urlshortener.service.encoder

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import ru.mplain.urlshortener.configuration.profiles.BASE_64
import java.util.*

@Service
@Profile(BASE_64)
class Base64EncoderService : EncoderService {

    override fun encode(long: Long): String =
        Base64.getUrlEncoder().encodeToString(long.toString().toByteArray()).removeSuffix(BASE_64_SUFFIX)

    override fun decode(string: String): Long =
        Base64.getUrlDecoder().decode(string.plus(BASE_64_SUFFIX)).let(::String).toLong()
}

private const val BASE_64_SUFFIX = "=="
