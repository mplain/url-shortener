package ru.mplain.urlshortener.configuration

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import ru.mplain.urlshortener.configuration.profiles.MONGO

@Configuration
@Profile("!$MONGO")
@EnableAutoConfiguration(exclude = [MongoReactiveAutoConfiguration::class, EmbeddedMongoAutoConfiguration::class])
class DisableMongoAutoConfiguration
