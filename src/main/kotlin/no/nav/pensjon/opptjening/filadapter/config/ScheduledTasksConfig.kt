package no.nav.pensjon.opptjening.filadapter.config

import io.getunleash.Unleash
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.repository.FilInfoRepository
import no.nav.pensjon.opptjening.filadapter.tasks.FinnNyeFilerTask
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableScheduling
@Profile("dev-gcp", "prod-gcp")
class ScheduledTasksConfig(
    private val unleash: Unleash,
    private val filInfoRepository: FilInfoRepository,
) {
    @Bean
    fun statusCheckTask(
        filsluseKlient: FilsluseKlient,
        filInfoRepository: FilInfoRepository
    ): FinnNyeFilerTask {
        return FinnNyeFilerTask(
            filInfoRepository = filInfoRepository,
            filsluseKlient = filsluseKlient
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