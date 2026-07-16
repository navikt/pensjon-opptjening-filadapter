package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagerstatusResponse
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class LagerstatusService(
    val poppKlient: PoppKlient,
    private val lagerstatusExecutor: ExecutorService,
) {
    companion object {
        private val log = LoggerFactory.getLogger(LagerstatusService::class.java)
    }

    fun erLagret(filnavn: String): Boolean {
        val lagerstatus = poppKlient.hentLagerstatus(filnavn)
        log.info("erLagret(filnavn=$filnavn): lagret:${lagerstatus.klarMedId} lagres:${lagerstatus.uferdigeMedId}")
        return lagerstatus.erKlar()
    }

    fun lagerStatus(filnavn: String): LagerstatusResponse {
        return poppKlient.hentLagerstatus(filnavn)
    }

    /**
     * Henter lagerstatus for flere filer parallelt. Hver fil krever et eget POPP-kall,
     * så et sekvensielt oppslag for mange filer blir tregt. Kallene kjøres på en
     * begrenset trådpool (se LagerstatusConfig) for å holde /list rask uten å oversvømme POPP.
     *
     * Rekkefølgen i svaret følger rekkefølgen i [filnavn]. Feiler ett POPP-kall, feiler hele
     * oppslaget (CompletableFuture.join kaster) — vi skjuler ikke feil bak en falsk status.
     */
    fun lagerStatusForFiler(filnavn: List<String>): List<LagerstatusResponse> {
        val futures = filnavn.map { navn ->
            CompletableFuture.supplyAsync({ poppKlient.hentLagerstatus(navn) }, lagerstatusExecutor)
        }
        return futures.map { it.join() }
    }
}
