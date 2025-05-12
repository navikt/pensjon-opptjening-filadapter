package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.RemoteFilInfo
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
    var utestående: MutableSet<RemoteFilInfo> = mutableSetOf()

    private fun finnFilerHvisIngenUtestående() {
        log.info("sjekker hvor mange filer som er cachet for overføring: ${utestående.size}")
        if (utestående.isEmpty()) {
            utestående.addAll(
                filsluseKlient.scanForFiles("/outbound")
            )
            log.info("utestående etter å ha blitt fyllt på med nye filer: ${utestående.size}")
            utestående.removeIf { it.size == 0L }
            log.info("utestående etter å ha fjernet tomme filer: ${utestående.size}")
            utestående.removeIf { lagerstatusService.erLagret(it.name) }
            log.info("utestående etter å ha fjernet allerede overførte filer: ${utestående.size}")
        }
    }

    @Synchronized
    fun overforEnUteståendeFil() {
        finnFilerHvisIngenUtestående()
        utestående.firstOrNull()?.let { filinfo ->
            val filnavn = filinfo.name
            log.info("overfører $filnavn")
            val resultat = prosesserFilService.overførFil(
                dir = "outbound",
                filnavn = filnavn,
                blockSize = 100_000
            )
            when (resultat.status) {
                ProsesserFilService.OverførResultat.Status.OK -> {
                    log.info("fil overført $filnavn")
                    utestående.remove(filinfo)
                }

                ProsesserFilService.OverførResultat.Status.FINNES_IKKE_I_FILSLUSE -> {
                    log.warn("fil forsøkt overført, men fantes ikke i filsluse: $filnavn")
                    utestående.remove(filinfo)
                }
                ProsesserFilService.OverførResultat.Status.FEILET -> {
                    log.error("overføring av fil feilet: $filnavn")
                }
            }
        }
    }
}