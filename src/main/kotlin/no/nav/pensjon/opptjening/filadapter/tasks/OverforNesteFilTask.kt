package no.nav.pensjon.opptjening.filadapter.tasks

import no.nav.pensjon.opptjening.filadapter.domain.OverforNesteFilService
import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import org.springframework.scheduling.annotation.Scheduled

class OverforNesteFilTask(
    private val overforNesteFilService: OverforNesteFilService,
) {
    companion object {
        val log = NAVLog(OverforNesteFilTask::class)
    }

    @Scheduled(fixedDelay = 30_000) // 30 sekunder
    fun overforNesteFil() {
        try {
            overforNesteFilService.overforEnUteståendeFil()
        } catch (t: Throwable) {
            log.open.info("OverforNesteFilTask feilet")
            log.secure.info("OverforNesteFilTask feilet", t)
        }
    }
}