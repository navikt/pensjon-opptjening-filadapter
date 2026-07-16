package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagerstatusResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LagerstatusServiceTest {

    private val executor = Executors.newFixedThreadPool(4)

    @AfterEach
    fun tearDown() {
        executor.shutdownNow()
    }

    private fun service(poppKlient: PoppKlient) = LagerstatusService(poppKlient, executor)

    @Test
    fun `lagerStatusForFiler beholder rekkefolgen fra input`() {
        val poppKlient = object : StubPoppKlient() {
            override fun hentLagerstatus(filnavn: String) =
                LagerstatusResponse(klarMedId = filnavn, uferdigeMedId = emptyList())
        }

        val resultat = service(poppKlient).lagerStatusForFiler(listOf("a", "b", "c", "d"))

        assertThat(resultat.map { it.klarMedId }).containsExactly("a", "b", "c", "d")
    }

    @Test
    fun `lagerStatusForFiler kaller popp for hver fil parallelt`() {
        val kalte = ConcurrentHashMap.newKeySet<String>()
        // Barriere med 4 parter (= poolstorrelsen). Hver hentLagerstatus blokkerer til fire
        // trader har mott opp samtidig. Ved sekvensiell kjoring ville forste kall vente for
        // alltid og await() ville time ut -> testen feiler. At await() gar gjennom BEVISER at
        // minst 4 kall kjorer overlappende.
        val barriere = CyclicBarrier(4)
        val poppKlient = object : StubPoppKlient() {
            override fun hentLagerstatus(filnavn: String): LagerstatusResponse {
                kalte.add(filnavn)
                barriere.await(5, TimeUnit.SECONDS)
                return LagerstatusResponse(klarMedId = null, uferdigeMedId = emptyList())
            }
        }

        // 20 filer / 4 trader = 5 fulle rendezvous; hvert krever ekte samtidighet
        val filnavn = (1..20).map { "fil-$it" }
        val resultat = service(poppKlient).lagerStatusForFiler(filnavn)

        assertThat(resultat).hasSize(20)
        assertThat(kalte).containsExactlyInAnyOrderElementsOf(filnavn)
    }

    @Test
    fun `lagerStatusForFiler med tom liste gjor ingen kall`() {
        val poppKlient = object : StubPoppKlient() {
            override fun hentLagerstatus(filnavn: String): LagerstatusResponse =
                throw AssertionError("skal ikke kalles")
        }

        assertThat(service(poppKlient).lagerStatusForFiler(emptyList())).isEmpty()
    }

    private abstract class StubPoppKlient : PoppKlient {
        override fun opprettFil(request: no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilRequest) =
            throw NotImplementedError()

        override fun validerFil(id: java.util.UUID) = throw NotImplementedError()

        override fun lagreFilSegment(request: no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentRequest) =
            throw NotImplementedError()
    }
}
