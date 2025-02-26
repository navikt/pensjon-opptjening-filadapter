package no.nav.pensjon.opptjening.filadapter.remote.popp

sealed class PoppClientException(message: String, throwable: Throwable?) : RuntimeException(message, throwable) {
    class FailedToParseResponse(
        message: String,
        throwable: Throwable?
    ) : PoppClientException(message, throwable)

    class ResponseWithNoBody(code: Int) : PoppClientException("Response with code: $code and no body", null)

    class KallFeilet(
        message: String,
        val response: Any
    ) : PoppClientException(message, null)
}