package no.nav.pensjon.opptjening.filadapter.remote.popp.domain

import java.util.*

data class OpprettFilResponse(
    val status: Status,
    val informasjonsTekst: String,
    val filId: UUID? = null,
) {
    enum class Status {
        LAGRET_OK,
        UGYLDIG_FILNAVN,
        MANGLER_FILNAVN,
        UGYLDIG_FILSEGMENT,
    }

    companion object {
        fun ok(filId: UUID): OpprettFilResponse {
            return OpprettFilResponse(
                status = Status.LAGRET_OK,
                informasjonsTekst = "Filen ble lagret ok",
                filId = filId,
            )
        }
    }
}