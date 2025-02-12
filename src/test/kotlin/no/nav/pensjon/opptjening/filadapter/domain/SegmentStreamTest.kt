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

}