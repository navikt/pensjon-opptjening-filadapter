package no.nav.pensjon.opptjening.filadapter.remote.popp.domain

import java.util.*

data class OpprettFilRequest(
    val fileName: String,
    val antallBytes: Long,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is OpprettFilRequest) {
            fileName == other.fileName
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(fileName)
    }
}
