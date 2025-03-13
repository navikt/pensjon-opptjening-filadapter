package no.nav.pensjon.opptjening.filadapter.remote.popp

import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.*
import java.util.*

interface PoppKlient {

    fun opprettFil(request: OpprettFilRequest): OpprettFilResponse
    fun validerFil(id: UUID): Boolean
    fun lagreFilSegment(lagreFilSegmentRequest: LagreFilSegmentRequest): LagreFilSegmentResponse
    fun hentLagerstatus(filnavn: String): LagerstatusResponse
}