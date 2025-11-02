package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentRequest
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentResponse
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilRequest
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilResponse
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class ProsesserFilService(
    val poppKlient: PoppKlient,
    val filsluseKlient: FilsluseKlient,
) {
    companion object {
        val log = NAVLog(ProsesserFilService::class)
    }

    fun overførFil(dir: String, filnavn: String, blockSize: Int): OverførResultat {
        return try {
            val filInfo = filsluseKlient.scanForFil(dir, filnavn) ?: return OverførResultat.finnesIkkeIFilsluse(filnavn)
            val poppFil = poppKlient.opprettFil(OpprettFilRequest(filnavn, filInfo.size))
            if (poppFil.filId == null) {
                throw ProsesserFilServiceException("Lagring av metadata for fil feilet: $filnavn")
            }
            val fileDownload = filsluseKlient.downloadFile(dir, filnavn)
            lagreSegmenter(fileDownload.getInputStream(), poppFil.filId, blockSize)
            fileDownload.close()
            val validertOk = poppKlient.validerFil(poppFil.filId)
            if (!validertOk) {
                log.open.error("Validering av fil feilet: $filnavn (${poppFil.filId}")
                OverførResultat.feilet(filnavn, poppFil.filId)
            } else {
                log.open.info("$filnavn er bekreftet overført til POPP")
                OverførResultat.ok(filnavn, poppFil.filId)
            }
        } catch (ex: Exception) {
            log.open.error("overførFil feilet")
            log.secure.error("overførFil feilet", ex)
            OverførResultat.feilet(filnavn)
        }
    }

    fun prosesser(fil: Path, blockSize: Int): UUID {
        sjekkAtFilErGyldig(fil)
        val response = opprettFil(fil)
        response.filId ?: throw ProsesserFilServiceException("Fikk ikke tilbake filId ved opprettelse: $response")
        lagreSegmenter(fil, response.filId, blockSize)
        poppKlient.validerFil(response.filId)
        return response.filId
    }

    private fun lagreSegmenter(fil: Path, filId: UUID, blockSize: Int) {
        lagreSegmenter(Files.newInputStream(fil), filId, blockSize)
    }

    private fun lagreSegmenter(inputStream: InputStream, filId: UUID, blockSize: Int) {
        val segmenter = SegmentStream(inputStream, blockSize)
        while (!segmenter.isAtEnd()) {
            val segment = segmenter.getSegment()
            val leggTilResponse = poppKlient.lagreFilSegment(
                request = LagreFilSegmentRequest(
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

    data class OverførResultat(
        val filId: UUID?,
        val filnavn: String,
        val status: Status,
    ) {
        enum class Status {
            OK,
            FINNES_IKKE_I_FILSLUSE,
            FEILET,
        }

        companion object {
            fun ok(filnavn: String, filId: UUID) = OverførResultat(filId, filnavn, Status.OK)
            fun finnesIkkeIFilsluse(filnavn: String) = OverførResultat(null, filnavn, Status.FINNES_IKKE_I_FILSLUSE)
            fun feilet(filnavn: String, filId: UUID? = null) = OverførResultat(filId, filnavn, Status.FEILET)
        }
    }
}