package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient

class LagerstatusService(
    val poppKlient: PoppKlient,
) {
    fun erLagret(filnavn: String): Boolean {
        return poppKlient.hentLagerstatus(filnavn).antallLagret > 0
    }
}