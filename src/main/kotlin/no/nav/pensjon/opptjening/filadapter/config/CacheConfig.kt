package no.nav.pensjon.opptjening.filadapter.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {

    companion object {
        const val CACHE_NAME = "lagerstatus"
        const val MAX_NUMBER_OF_ENTRIES: Long = 5000
        const val DURATION_HOURS: Long = 1
        private val log: Logger = LoggerFactory.getLogger(CacheConfig::class.java)
    }

    @Bean
    fun cacheManager(): CacheManager {
        val caffeineCacheManager = CaffeineCacheManager(CACHE_NAME)
        caffeineCacheManager.setCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(DURATION_HOURS, TimeUnit.HOURS)
                .maximumSize(MAX_NUMBER_OF_ENTRIES)
        )

        log.info("Cache Manager initialization complete with cache name: $CACHE_NAME, max entries: $MAX_NUMBER_OF_ENTRIES, duration: $DURATION_HOURS hours")

        return caffeineCacheManager
    }
}