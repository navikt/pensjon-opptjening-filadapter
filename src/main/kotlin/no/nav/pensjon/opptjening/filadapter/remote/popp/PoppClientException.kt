package no.nav.pensjon.opptjening.filadapter.remote.popp

import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentResponse
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilResponse
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.ValiderFilResponse

sealed class PoppClientException(message: String, throwable: Throwable?) : RuntimeException(message, throwable) {
    class FailedToParseResponse(
        message: String,
        throwable: Throwable?
    ) : PoppClientException(message, throwable)

    class ResponseWithNoBody(val code: Int) : PoppClientException("Response with code: $code and no body", null)

    class OpprettFilFeilet(
        message: String,
        val response: OpprettFilResponse
    ) : PoppClientException(message, null)

    class LagreSegmentFeilet(
        message: String,
        val response: LagreFilSegmentResponse
    ) : PoppClientException(message, null)

    class KallFeilet(
        message: String,
        val response: Any
    ) : PoppClientException(message, null)
}