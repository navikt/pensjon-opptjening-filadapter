package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OverforNesteFilService(
    val filsluseKlient: FilsluseKlient,
    val lagerstatusService: LagerstatusService,
    val prosesserFilService: ProsesserFilService,
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
            log.info("overfører $it")
            val resultat = prosesserFilService.overførFil(
                dir = "outbound",
                filnavn = it,
                blockSize = 100_000
            )
            when (resultat.status) {
                ProsesserFilService.OverførResultat.Status.OK -> {
                    log.info("fil overført $it")
                    utestående.remove(it)
                }

                ProsesserFilService.OverførResultat.Status.FINNES_IKKE_I_FILSLUSE -> {
                    log.warn("fil forsøkt overført, men fantes ikke i filsluse: $it")
                    utestående.remove(it)
                }
                ProsesserFilService.OverførResultat.Status.FEILET -> {
                    log.error("overføring av fil feilet: $it")
                }
            }
        }
    }
}