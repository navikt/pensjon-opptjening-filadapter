package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.config.CacheConfig
import no.nav.pensjon.opptjening.filadapter.config.MockPoppKlientConfig
import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagerstatusResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager


@SpringBootTest(classes = [LagerstatusService::class, CacheConfig::class, MockPoppKlientConfig::class])
class LagerstatusServiceCacheTest {

    @Autowired
    private lateinit var lagerstatusService: LagerstatusService

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Autowired
    private lateinit var poppKlient: PoppKlient


    @BeforeEach
    fun setup() {
        cacheManager.getCache("lagerstatus")?.clear()
    }

    @Test
    fun `erLagret should cache the result from poppKlient`() {
        val filnavn = "cached-file.txt"
        whenever(poppKlient.hentLagerstatus(filnavn)).thenReturn(
            LagerstatusResponse(antallLagret = 0, antallLagres = 0)
        )

        lagerstatusService.erLagret(filnavn)
        lagerstatusService.erLagret(filnavn)

        verify(poppKlient, times(1)).hentLagerstatus(filnavn)
    }

    @Test
    fun `erLagret should not use cache for different filenames`() {
        val filnavn1 = "file1.txt"
        val filnavn2 = "file2.txt"
        whenever(poppKlient.hentLagerstatus(filnavn1)).thenReturn(
            LagerstatusResponse(antallLagret = 0, antallLagres = 0)
        )
        whenever(poppKlient.hentLagerstatus(filnavn2)).thenReturn(
            LagerstatusResponse(antallLagret = 0, antallLagres = 0)
        )

        lagerstatusService.erLagret(filnavn1)
        lagerstatusService.erLagret(filnavn2)

        verify(poppKlient, times(1)).hentLagerstatus(filnavn1)
        verify(poppKlient, times(1)).hentLagerstatus(filnavn2)
    }
}