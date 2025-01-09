package no.nav.pensjon.opptjening.filadapter.domain

import java.util.*

data class FilInfo(
    val filnavn: String,
    val filId: UUID?,
    val status: Status,
) {
    enum class Status {
        NY,
        LAGRES_I_POPP,
        LAGRET,
        VALIDERT,
        FEILET,
    }

    companion object {
        fun ny(filnavn: String) = FilInfo(filnavn, null, Status.NY)
    }
}