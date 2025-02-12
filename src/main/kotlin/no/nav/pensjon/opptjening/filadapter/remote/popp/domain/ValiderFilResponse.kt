package no.nav.pensjon.opptjening.filadapter.remote.popp.domain

data class ValiderFilResponse(
    val status: Status,
    val antallSegmenter: Int?,
    val oppgittAntallBytes: Long?,
    val faktiskAntallBytes: Long?,
) {
    enum class Status {
        OK,
        FINNES_IKKE,
        IKKE_KONTINUERLIG,
        FEIL_ANTALL_BYTES,
        INGEN_SEGMENTER,
    }
}