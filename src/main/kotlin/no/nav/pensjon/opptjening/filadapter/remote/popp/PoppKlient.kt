package no.nav.pensjon.opptjening.filadapter.remote.popp

import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentRequest
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentResponse
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilRequest
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilResponse
import java.util.*

interface PoppKlient {

    fun opprettFil(request: OpprettFilRequest): OpprettFilResponse
    fun validerFil(id: UUID): Boolean
    fun lagreFilSegment(lagreFilSegmentRequest: LagreFilSegmentRequest): LagreFilSegmentResponse
}