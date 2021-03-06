package ru.mplain.urlshortener.service.encoder

import org.hashids.Hashids
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import ru.mplain.urlshortener.configuration.profiles.HASHIDS

@Service
@Profile(HASHIDS)
class HashIdsEncoderService : EncoderService {

    private val hashids = Hashids("this is my salt")

    override fun encode(long: Long): String =
        hashids.encode(long)

    override fun decode(string: String): Long =
        hashids.decode(string).single()
}
