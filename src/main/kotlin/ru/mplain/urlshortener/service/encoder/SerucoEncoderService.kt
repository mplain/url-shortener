package ru.mplain.urlshortener.service.encoder

import io.seruco.encoding.base62.Base62
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("seruco")
class SerucoEncoderService : EncoderService {

    private val base62 = Base62.createInstance()

    override fun encode(long: Long): String =
        String(base62.encode(long.toString().toByteArray()))

    override fun decode(string: String): Long =
        String(base62.decode(string.toByteArray())).toLong()
}
