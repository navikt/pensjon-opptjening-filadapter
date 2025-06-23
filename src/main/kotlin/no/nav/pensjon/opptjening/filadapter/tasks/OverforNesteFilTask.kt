package no.nav.pensjon.opptjening.filadapter.tasks

import no.nav.pensjon.opptjening.filadapter.domain.OverforNesteFil
import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import org.springframework.scheduling.annotation.Scheduled

class OverforNesteFilTask(
    private val overforNesteFil: OverforNesteFil,
) {
    companion object {
        val log = NAVLog(OverforNesteFilTask::class)
    }

    @Scheduled(fixedDelay = 30_000) // 30 sekunder
    fun overforNesteFilService() {
        try {
            overforNesteFil.overforEnUteståendeFil()
        } catch (t: Throwable) {
            log.open.info("OverforNesteFilTask feilet")
            log.secure.info("OverforNesteFilTask feilet", t)
        }
    }
}