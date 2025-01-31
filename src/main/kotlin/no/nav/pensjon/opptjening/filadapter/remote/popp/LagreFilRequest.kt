package no.nav.pensjon.opptjening.filadapter.remote.popp

import java.util.*

data class LagreFilRequest(
    val fileName: String,
    val tegnsett: Tegnsett,
    val antallBytes: Long,
    val data: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is LagreFilRequest) {
            fileName == other.fileName
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(fileName)
    }

    enum class Tegnsett {
        EBCDIC,
        US_ASCII,
    }
}
