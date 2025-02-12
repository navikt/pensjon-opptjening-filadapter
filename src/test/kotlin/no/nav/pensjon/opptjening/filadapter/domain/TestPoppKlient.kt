package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentRequest
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentResponse
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilRequest
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilResponse
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.*

class TestPoppKlient : PoppKlient {
    class StreamQueue {
        val outputStream = PipedOutputStream()
        val inputStream = PipedInputStream(outputStream)
    }

    val fileData: MutableMap<UUID, StreamQueue> = mutableMapOf()
    val fileInfo: MutableMap<UUID, OpprettFilRequest> = mutableMapOf()
    val filInnhold: MutableMap<UUID, ByteArray> = mutableMapOf()

    override fun opprettFil(request: OpprettFilRequest): OpprettFilResponse {
        val uuid = UUID.randomUUID()
        fileData[uuid] = StreamQueue()
        fileInfo[uuid] = request
        return OpprettFilResponse(
            status = OpprettFilResponse.Status.LAGRET_OK,
            informasjonsTekst = "Lagret ok",
            filId = uuid,
        )
    }

    override fun validerFil(id: UUID): Boolean {
        val antallBytes = fileInfo[id]!!.antallBytes.toInt()
        fileData[id]!!.outputStream.close()
        val innhold = fileData[id]!!.inputStream.readAllBytes()
        filInnhold[id] = innhold
        return antallBytes == innhold.size
    }

    override fun lagreFilSegment(request: LagreFilSegmentRequest): LagreFilSegmentResponse {
        fileData[request.filId]!!.outputStream.write(request.data)
        return LagreFilSegmentResponse.ok()
    }

    fun hentInnhold(id: UUID): ByteArray? {
        return filInnhold[id]
    }
}