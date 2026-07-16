package no.nav.pensjon.opptjening.filadapter.web

import no.nav.pensjon.opptjening.filadapter.domain.LagerstatusService
import no.nav.pensjon.opptjening.filadapter.domain.ProsesserFilService
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.RemoteFilInfo
import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagerstatusResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.time.Instant
import java.util.concurrent.Executors

class AdminWebApiPaginationTest {

    private val executor = Executors.newFixedThreadPool(4)

    @AfterEach
    fun tearDown() {
        executor.shutdownNow()
    }

    // POPP-stub som setter klarMedId = filnavn, så vi kan verifisere at hver fil
    // pares med sin egen status etter parallell henting.
    private val poppKlient = object : StubPoppKlient() {
        override fun hentLagerstatus(filnavn: String) =
            LagerstatusResponse(klarMedId = filnavn, uferdigeMedId = emptyList())
    }

    private fun api(antallFiler: Int): AdminWebApi {
        val basis = Instant.parse("2025-01-01T00:00:00Z")
        // fil-00 eldst, fil-(n-1) nyest — så nyeste-først sortering er deterministisk
        val filer = (0 until antallFiler).map { i ->
            RemoteFilInfo(name = "fil-%02d".format(i), size = 100L + i, modifiedAt = basis.plusSeconds(i * 60L))
        }
        val filslusa = object : FilsluseKlient {
            override fun scanForFiles(remoteDir: String) = filer
            override fun scanForFil(remoteDir: String, filnavn: String) = throw NotImplementedError()
            override fun downloadFile(remoteDir: String, fileName: String) = throw NotImplementedError()
        }
        return AdminWebApi(
            filsluseKlient = filslusa,
            prosesserFilService = mock<ProsesserFilService>(),
            lagerstatusService = LagerstatusService(poppKlient, executor),
        )
    }

    @Test
    fun `forste side gir nyeste filer forst og harMer true`() {
        val svar = api(60).listFiler(side = 0, antall = 25).body!!

        assertThat(svar.totalt).isEqualTo(60)
        assertThat(svar.side).isEqualTo(0)
        assertThat(svar.antall).isEqualTo(25)
        assertThat(svar.harMer).isTrue()
        assertThat(svar.filer).hasSize(25)
        // nyeste (fil-59) først, deretter synkende
        assertThat(svar.filer.first().filnavn).isEqualTo("fil-59")
        assertThat(svar.filer.map { it.filnavn }).isEqualTo((59 downTo 35).map { "fil-%02d".format(it) })
    }

    @Test
    fun `andre side gir neste vindu uten overlapp`() {
        val side0 = api(60).listFiler(side = 0, antall = 25).body!!.filer.map { it.filnavn }
        val side1 = api(60).listFiler(side = 1, antall = 25).body!!.filer.map { it.filnavn }

        assertThat(side1).isEqualTo((34 downTo 10).map { "fil-%02d".format(it) })
        assertThat(side0).doesNotContainAnyElementsOf(side1)
    }

    @Test
    fun `siste side har farre filer og harMer false`() {
        val svar = api(60).listFiler(side = 2, antall = 25).body!!

        assertThat(svar.filer).hasSize(10)
        assertThat(svar.harMer).isFalse()
        assertThat(svar.filer.last().filnavn).isEqualTo("fil-00")
    }

    @Test
    fun `hver fil pares med sin egen popp-status`() {
        val svar = api(60).listFiler(side = 1, antall = 25).body!!

        // stubben setter klarMedId = filnavn — verifiserer at zip ikke stokker om rekkefolgen
        assertThat(svar.filer).allSatisfy { assertThat(it.lagretMedId).isEqualTo(it.filnavn) }
    }

    @Test
    fun `antall klemmes til lovlig omrade og negativ side blir null`() {
        val forStort = api(60).listFiler(side = 0, antall = 500).body!!
        assertThat(forStort.antall).isEqualTo(200) // 500 klemt til MAKS_ANTALL=200
        assertThat(forStort.filer).hasSize(60) // bare 60 finnes
        assertThat(forStort.harMer).isFalse()

        val forLite = api(60).listFiler(side = 0, antall = 0).body!!
        assertThat(forLite.antall).isEqualTo(1)
        assertThat(forLite.filer).hasSize(1)

        val negativSide = api(60).listFiler(side = -5, antall = 25).body!!
        assertThat(negativSide.side).isEqualTo(0)
        assertThat(negativSide.filer.first().filnavn).isEqualTo("fil-59")
    }

    @Test
    fun `tom filsluse gir tomt svar uten harMer`() {
        val svar = api(0).listFiler(side = 0, antall = 25).body!!

        assertThat(svar.filer).isEmpty()
        assertThat(svar.totalt).isEqualTo(0)
        assertThat(svar.harMer).isFalse()
    }

    private abstract class StubPoppKlient : PoppKlient {
        override fun opprettFil(request: no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilRequest) =
            throw NotImplementedError()

        override fun validerFil(id: java.util.UUID) = throw NotImplementedError()

        override fun lagreFilSegment(request: no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentRequest) =
            throw NotImplementedError()
    }
}
