package no.nav.popp.web.api.endpoint.fil.model

import java.util.*

data class LagreFilResponse(
    val status: Status,
    val informasjonsTekst: String,
    val filId: UUID? = null,
) {
    enum class Status {
        LAGRET_OK,
        UGYLDIG_FILNAVN,
        MANGLER_FILNAVN,
        UGYLDIG_TEGNSETT,
        MANGLER_TEGNSETT,
        UGYLDIG_FILSEGMENT,
    }

    companion object {
        fun ok(filId: UUID): LagreFilResponse {
            return LagreFilResponse(
                status = Status.LAGRET_OK,
                informasjonsTekst = "Filen ble lagret ok",
                filId = filId,
            )
        }
    }
}