package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.domain.OverforNesteFil
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.tasks.FinnNyeFilerTask
import no.nav.pensjon.opptjening.filadapter.tasks.OverforNesteFilTask
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableScheduling
@Profile("dev-fss", "prod-fss")
class ScheduledTasksConfig {

    @Bean
    fun statusCheckTask(
        filsluseKlient: FilsluseKlient,
    ): FinnNyeFilerTask {
        return FinnNyeFilerTask(
            filsluseKlient = filsluseKlient,
        )
    }

    @Bean
    fun overforNesteFilerService(
        overforNesteFil: OverforNesteFil,
    ): OverforNesteFilTask {
        return OverforNesteFilTask(
            overforNesteFil,
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