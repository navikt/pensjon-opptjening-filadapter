package no.nav.pensjon.opptjening.filadapter.repository

import java.time.LocalDateTime

data class FilInfo(
    val id: Id,
    val dato: LocalDateTime,
    val status: Status,
) {
    data class Id(
        val filnavn: String
    )

    enum class Status {
        NY,
        IKKE_BEHANDLES,
        LAGRET,
        FEILET,
    }
}