package no.nav.pensjon.opptjening.filadapter.web.dto

data class OverforFilResponse(
    val status: Status,
    val melding: String,
) {
    companion object {
        fun ok(): OverforFilResponse = OverforFilResponse(Status.OK, "Fil overført")
        fun feilet(msg: String): OverforFilResponse = OverforFilResponse(Status.FEILET, msg)
    }
    enum class Status {
        OK,
        FEILET,
    }
}