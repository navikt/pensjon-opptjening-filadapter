package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagerstatusResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LagerstatusService(
    val poppKlient: PoppKlient,
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
}