package no.nav.pensjon.opptjening.filadapter.tasks

import no.nav.pensjon.opptjening.filadapter.domain.OverforNesteFilService
import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit

class OverforNesteFilTask(
    private val overforNesteFilService: OverforNesteFilService,
) {
    companion object {
        val log = NAVLog(OverforNesteFilTask::class)
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    fun overforNesteFil() {
        try {
            overforNesteFilService.overførEnUteståendeFil()
        } catch (t: Throwable) {
            log.open.info("OverforNesteFilTask feilet")
            log.secure.info("OverforNesteFilTask feilet", t)
        }
    }
}