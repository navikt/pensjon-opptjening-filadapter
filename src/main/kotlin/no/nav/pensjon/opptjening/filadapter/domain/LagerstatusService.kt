package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient

class LagerstatusService(
    val poppKlient: PoppKlient,
) {
    val cache: MutableMap<String, Boolean> = mutableMapOf()

//    data class Status(val lagret: Boolean, val sjekket: LocalDateTime)

    fun erLagret(filnavn: String) : Boolean {
        if (!cache.containsKey(filnavn)) {
            val status = poppKlient.hentLagerstatus(filnavn)
            cache[filnavn] = status.antallLagret > 0
        }
        return cache[filnavn]!!
    }

    fun settLagret(filnavn: String) {
        cache[filnavn] = true
    }

    fun clearCache() {
        cache.clear()
    }
}