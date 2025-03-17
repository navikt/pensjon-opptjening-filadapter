package no.nav.pensjon.opptjening.filadapter.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class SegmentStreamTest {
    // Viktig for testene å ikke ha med norske tegn, da de tegnene er multibyte
    val LANG_STRENG = "Dette er en veldig lang streng og den er ikke ferdig enda men na er den ferdig"

    @Test
    fun `kan lese en inputstream i mange segmenter`() {
        val segmentStream = SegmentStream(LANG_STRENG.byteInputStream(charset = StandardCharsets.UTF_8), 16)
        val builder = StringBuilder()
        while (!segmentStream.isAtEnd()) {
            val segment = segmentStream.getSegment()
            segmentStream.next()
            val segmentSomStreng = String(segment.bytes, StandardCharsets.UTF_8)
            println("Leste segment: $segmentSomStreng")
            builder.append(segmentSomStreng)
        }
        assertThat(builder.toString()).isEqualTo(LANG_STRENG)
    }

    @Test
    fun `kan lese en inputstream i store segmenter`() {
        val innhold = "abcdefghijklmnopqrstuvwxyz".repeat(5)
        val segmentStream =
            innhold
            .byteInputStream(charset = StandardCharsets.UTF_8)
            .let { LimitedInputStream(it, 17) }
            .let { SegmentStream(it, 40) }
        val segment1 = segmentStream.getSegment()
        segmentStream.next()
        val segment2 = segmentStream.getSegment()
        segmentStream.next()
        val segment3 = segmentStream.getSegment()
        segmentStream.next()
        val segment4 = segmentStream.getSegment()
        segmentStream.next()
        val isEnded = segmentStream.isAtEnd()
        val part1 = String(segment1.bytes, StandardCharsets.UTF_8)
        val part2 = String(segment2.bytes, StandardCharsets.UTF_8)
        val part3 = String(segment3.bytes, StandardCharsets.UTF_8)
        val part4 = String(segment4.bytes, StandardCharsets.UTF_8)

        val resultat = part1 + part2 + part3 + part4
        assertThat(resultat).isEqualTo(innhold)

        assertThat(segment1.bytes.size).isEqualTo(40)
        assertThat(segment2.bytes.size).isEqualTo(40)
        assertThat(segment3.bytes.size).isEqualTo(40)
        assertThat(segment4.bytes.size).isEqualTo(10)
        assertThat(isEnded).isTrue()
    }
}