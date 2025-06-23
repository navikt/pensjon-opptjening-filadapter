package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class LagerstatusService(
    val poppKlient: PoppKlient,
) {
    @Cacheable("lagerstatus")
    fun erLagret(filnavn: String): Boolean {
        return poppKlient.hentLagerstatus(filnavn).antallLagret > 0
    }
}