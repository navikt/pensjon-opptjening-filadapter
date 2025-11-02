package no.nav.pensjon.opptjening.filadapter.remote.popp.domain

data class LagerstatusResponse(
    val klarMedId: String?,
    val uferdigeMedId: List<String>,
) {
    fun erKlar(): Boolean {
        return klarMedId != null
    }

    fun antallUferdige(): Int {
        return uferdigeMedId.size
    }
}