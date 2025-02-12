package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentRequest
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentResponse
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilRequest
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilResponse
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class ProsesserFilService(val poppKlient: PoppKlient) {

    fun prosesser(fil: Path): UUID {
        sjekkAtFilErGyldig(fil)
        val response = opprettFil(fil)
        response.filId ?: throw ProsesserFilServiceException("Fikk ikke tilbake filId ved opprettelse: $response")
        lagreSegmenter(fil, response.filId)
        poppKlient.validerFil(response.filId)
        return response.filId
    }

    private fun lagreSegmenter(fil: Path, filId: UUID) {
        val segmenter = SegmentStream(Files.newInputStream(fil), 8)
        while (!segmenter.isAtEnd()) {
            val segment = segmenter.getSegment()
            val leggTilResponse = poppKlient.lagreFilSegment(
                lagreFilSegmentRequest = LagreFilSegmentRequest(
                    filId = filId,
                    segmentNo = segment.segmentNo,
                    data = segment.bytes,
                )
            )
            if (leggTilResponse.status != LagreFilSegmentResponse.Status.OK) {
                throw ProsesserFilServiceException("Kunne ikke legge til segment: $leggTilResponse")
            }
            segmenter.next()
        }
    }

    private fun opprettFil(fil: Path): OpprettFilResponse {
        val size = Files.size(fil)
        val response = poppKlient.opprettFil(
            OpprettFilRequest(
                fileName = fil.fileName.toString(),
                antallBytes = size
            )
        )
        return response
    }

    private fun sjekkAtFilErGyldig(fil: Path) {
        if (!Files.isRegularFile(fil)) {
            throw ProsesserFilServiceException("$fil er ikke en gyldig fil")
        }
    }

    class ProsesserFilServiceException(message: String) : RuntimeException(message)
}