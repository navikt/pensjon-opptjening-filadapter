package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
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
        log.info("erLagret(filnavn=$filnavn): lagret:${lagerstatus.antallLagret} lagres:${lagerstatus.antallLagres}")
        return lagerstatus.antallLagret > 0
    }
}