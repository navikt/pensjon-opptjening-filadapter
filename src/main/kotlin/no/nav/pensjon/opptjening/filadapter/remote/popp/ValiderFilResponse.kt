package no.nav.popp.web.api.endpoint.fil.model

data class ValiderFilResponse(
    val status: Status,
    val oppgittAntallBytes: Long,
    val faktiskAntallBytes: Long,
) {
    enum class Status {
        OK,
        UGYLDIG,
    }
}