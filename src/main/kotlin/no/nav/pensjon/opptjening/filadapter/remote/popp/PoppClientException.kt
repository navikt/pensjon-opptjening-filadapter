package no.nav.pensjon.opptjening.filadapter.remote.popp

sealed class PoppClientException(message: String, throwable: Throwable?) : RuntimeException(message, throwable) {
    class LagreFilFeilet(message: String, throwable: Throwable?) : PoppClientException(message, throwable)
}