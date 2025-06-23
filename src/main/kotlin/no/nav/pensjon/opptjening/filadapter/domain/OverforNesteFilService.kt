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
        private val log: Logger = LoggerFactory.getLogger(OverforNesteFilService::class.java)
    }

    var utestående: MutableSet<RemoteFilInfo> = mutableSetOf()
    var alleredeProsessert: MutableSet<RemoteFilInfo> = mutableSetOf()

    private fun finnFilerHvisIngenUtestående() {
        if (utestående.isEmpty()) {
            val kandidater = filsluseKlient.scanForFiles("/outbound")
                .asSequence()
                .filterNot { it in alleredeProsessert }
                .filter { it.size > 0L }
                .toList()

            val (alleredeLagret, nyeUtestående) = kandidater.partition { lagerstatusService.erLagret(it.name) }

            log.info("Fant ${kandidater.size} kandidatfiler. ${alleredeLagret.size} var allerede overført, ${nyeUtestående.size} nye filer lagt til for overføring.")

            alleredeProsessert.addAll(alleredeLagret)
            utestående.addAll(nyeUtestående)
        }

        log.info("Totalt har ${alleredeProsessert.size} filer blitt prosessert. ${utestående.size} venter på overføring.")
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