package ru.mplain.urlshortener.service.dao

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.FindAndModifyOptions.options
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import ru.mplain.urlshortener.configuration.profiles.MONGO
import ru.mplain.urlshortener.model.MongoSequence
import ru.mplain.urlshortener.model.SHORTENED_URL_SEQ
import ru.mplain.urlshortener.model.ShortenedUrl
import ru.mplain.urlshortener.repository.MongoRepository
import java.util.concurrent.atomic.AtomicLong

@Service
@Profile(MONGO)
class MongoDao(
    private val repository: MongoRepository,
    private val template: ReactiveMongoTemplate
) : UrlShortenerDao {

    private val mutex = Mutex()
    private val batchStart = AtomicLong(0)
    private val batchCounter = AtomicLong(BATCH_SIZE)

    override suspend fun nextval(): Long {
        val value = batchCounter.getAndIncrement()
        return if (value >= BATCH_SIZE) {
            fetchNextBatch()
            nextval()
        } else {
            batchStart.get() + value
        }
    }

    private suspend fun fetchNextBatch() = mutex.withLock {
        if (batchCounter.get() >= BATCH_SIZE) {
            val seq = template.findAndModify(query, update, options, MongoSequence::class.java).awaitSingle().seq
            batchStart.set(seq)
            batchCounter.set(0)
        }
    }

    override suspend fun save(id: String, url: String): String =
        repository.insert(ShortenedUrl(id, url)).awaitSingle().id

    override suspend fun getById(id: String): String? =
        repository.findById(id).awaitSingleOrNull()?.url

    override suspend fun reset() {
        repository.deleteAll().awaitFirstOrNull()
        template.findAndRemove(query, MongoSequence::class.java).awaitFirstOrNull()
    }
}

private const val BATCH_SIZE = 1000L

private val query = query(where("_id").`is`(SHORTENED_URL_SEQ))
private val update = Update().inc(MongoSequence::seq.name, BATCH_SIZE)
private val options = options().returnNew(true).upsert(true)
