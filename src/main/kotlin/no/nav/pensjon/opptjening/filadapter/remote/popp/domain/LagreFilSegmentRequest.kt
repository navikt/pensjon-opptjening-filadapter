package no.nav.pensjon.opptjening.filadapter.remote.popp.domain

import java.util.*

data class LagreFilSegmentRequest(
    val filId: UUID,
    val segmentNo: Int,
    val data: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)

    }
}
