package no.nav.pensjon.opptjening.filadapter.domain

import java.io.InputStream

class SegmentStream(val inputStream: InputStream, val blockSize: Int) {

    private val buffer = ByteArray(blockSize)

    private var segment: Segment? = readNextBytes(0)

    fun isAtEnd(): Boolean {
        return segment == null
    }

    fun getSegment(): Segment {
        return segment ?: throw SegmentStreamException("Forsøk på å lese fra en ferdig konsumert stream")
    }

    fun next() {
        if (segment != null) {
            segment = readNextBytes(segment!!.segmentNo + 1)
        }
    }

    private fun readNextBytes(segmentNo: Int): Segment? {
        val readBytes = inputStream.read(buffer, 0, blockSize)
        return if (readBytes == -1) {
            null
        } else {
            val bytes = ByteArray(readBytes)
            System.arraycopy(buffer, 0, bytes, 0, readBytes)
            Segment(segmentNo, bytes)
        }
    }

    data class Segment(
        val segmentNo: Int,
        val bytes: ByteArray
    ) {
        override fun toString(): String {
            return "Segment($segmentNo,[...])"
        }

        override fun hashCode(): Int {
            return System.identityHashCode(this)
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }
    }

    class SegmentStreamException(msg: String) : Exception(msg)
}