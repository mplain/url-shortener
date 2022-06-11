package ru.mplain.urlshortener.service.dao

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.FindAndModifyOptions.options
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import ru.mplain.urlshortener.configuration.profiles.MONGO
import ru.mplain.urlshortener.model.MongoSequence
import ru.mplain.urlshortener.model.SHORTENED_URL_SEQ
import ru.mplain.urlshortener.model.ShortenedUrl
import ru.mplain.urlshortener.repository.MongoRepository

@Service
@Profile(MONGO)
class MongoDao(
    private val repository: MongoRepository,
    private val mongoOperations: ReactiveMongoOperations
) : UrlShortenerDao {

    private val query = query(where("_id").`is`(SHORTENED_URL_SEQ))

    override suspend fun incrementSequence(add: Long): Long {
        val update = Update().inc(MongoSequence::seq.name, add)
        val options = options().returnNew(true).upsert(true)
        return mongoOperations.findAndModify(query, update, options, MongoSequence::class.java).awaitSingle().seq
    }

    override suspend fun save(id: String, url: String): String =
        repository.save(ShortenedUrl(id, url)).awaitSingle().id

    override suspend fun getById(id: String): String? =
        repository.findById(id).awaitSingleOrNull()?.url

    override suspend fun reset() {
        repository.deleteAll().awaitSingle()
        mongoOperations.findAndRemove(query, MongoSequence::class.java).awaitSingle()
    }
}
