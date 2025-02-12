package no.nav.pensjon.opptjening.filadapter.remote.popp.domain

data class LagreFilSegmentResponse(
    val status: Status,
) {
    enum class Status {
        OK,
        ERROR,
    }

    companion object {
        fun ok() = LagreFilSegmentResponse(Status.OK)
        fun error() = LagreFilSegmentResponse(Status.ERROR)
    }
}