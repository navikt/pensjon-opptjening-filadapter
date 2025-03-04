package no.nav.pensjon.opptjening.filadapter.domain

import java.io.InputStream
import kotlin.math.min

class LimitedInputStream(private val inputStream: InputStream, private val limit: Int) : InputStream() {
    override fun read(): Int {
        return inputStream.read()
    }

    override fun read(b: ByteArray): Int {
        return inputStream.read(b, 0, min(b.size, limit))
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return inputStream.read(b, off, min(len, limit))
    }

    override fun available(): Int {
        return inputStream.available()
    }
}