package ru.mplain.urlshortener.service.encoder

import com.google.common.math.IntMath.pow
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import ru.mplain.urlshortener.configuration.profiles.BASE_62

@Service
@Profile(BASE_62)
class Base62EncoderService : EncoderService {

    override fun encode(long: Long): String {
        var n = long
        val result = StringBuilder()
        while (n > 0) {
            val remainder = (n % BASE62).toInt()
            result.append(BASE62_ALPHABET[remainder])
            n /= BASE62
        }
        return result.toString()
    }

    override fun decode(string: String): Long =
        string.foldIndexed(0L) { i, acc, c -> acc + BASE62_ALPHABET.indexOf(c) * pow(BASE62, i) }
}

private const val BASE62 = 62
private const val BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
