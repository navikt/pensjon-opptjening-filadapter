package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OverforNesteFilService(
    val filsluseKlient: FilsluseKlient,
    val lagerstatusService: LagerstatusService,
) {
    companion object {
        private val log : Logger = LoggerFactory.getLogger(OverforNesteFilService::class.java)
    }
    var utestående: MutableSet<String> = mutableSetOf()

    private fun finnFilerHvisIngenUtestående() {
        log.info("sjekker hvor mange filer som er cachet for overføring: ${utestående.size}")
        if (utestående.isEmpty()) {
            utestående.addAll(
                filsluseKlient.scanForFiles("/outbound").map { it.name }
            )
            log.info("utestående etter å ha blitt fyllt på med nye filer: ${utestående.size}")
            utestående.removeIf { lagerstatusService.erLagret(it) }
            log.info("utestående etter å ha fjernet allerede overførte filer: ${utestående.size}")
        }
    }

    @Synchronized
    fun overforEnUteståendeFil() {
        finnFilerHvisIngenUtestående()
        utestående.firstOrNull()?.let {
            log.info("overfører $it (simulert)")
            utestående.remove(it)
        }
    }
}