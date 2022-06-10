package ru.mplain.urlshortener.service.encoder

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("base62")
class Base62EncoderService : EncoderService {

    override fun encode(long: Long): String {
        var n = long
        val result = StringBuilder()
        while (n > 0) {
            val remainder = (n % 62).toInt()
            result.append(BASE_62[remainder])
            n /= 62
        }
        return result.toString()
    }

    override fun decode(string: String): Long {
        TODO("Not yet implemented")
    }
}

private const val BASE_62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
