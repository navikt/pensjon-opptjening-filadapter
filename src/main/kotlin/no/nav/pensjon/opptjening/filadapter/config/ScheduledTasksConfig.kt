package no.nav.pensjon.opptjening.filadapter.config

import io.getunleash.Unleash
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseClient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseClientImpl
import no.nav.pensjon.opptjening.filadapter.repository.FilInfoRepository
import no.nav.pensjon.opptjening.filadapter.tasks.FinnNyeFilerTask
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.lang.Integer.parseInt
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableScheduling
@Profile("dev-gcp", "prod-gcp")
class ScheduledTasksConfig(
    private val unleash: Unleash,
    private val filInfoRepository: FilInfoRepository,
) {
    @Bean
    fun filsluseClient(
        @Value("\${FILSLUSE_HOST}") host: String,
        @Value("\${FILSLUSE_PORT}") port: String,
        @Value("\${FILSLUSE_USERNAME}") username: String,
        @Value("\${SSH_PRIVATE_KEY}") privateKey: String,
        @Value("\${SSH_PRIVATE_KEY_PASSPHRASE}") passphrase: String,
    ): FilsluseClient {
        return FilsluseClientImpl(
            host = host,
            port = parseInt(port),
            username = username,
            privateKey = privateKey,
            privateKeyPassword = passphrase
        )
    }

    @Bean
    fun statusCheckTask(
        filsluseClient: FilsluseClient,
        filInfoRepository: FilInfoRepository
    ): FinnNyeFilerTask {
        return FinnNyeFilerTask(
            filInfoRepository = filInfoRepository,
            filsluseClient = filsluseClient
        )
    }

    @Bean("scheduledTasksExecutor")
    fun threadpoolExecutor(): ThreadPoolTaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            queueCapacity = 25
            corePoolSize = 1
            maxPoolSize = 5
            setThreadNamePrefix("ScheduledTasksExecutor-")
            setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
            setAllowCoreThreadTimeOut(true)
            keepAliveSeconds = 60
            initialize()
        }
    }
}