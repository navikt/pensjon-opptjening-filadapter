package no.nav.pensjon.opptjening.filadapter.remote.popp

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