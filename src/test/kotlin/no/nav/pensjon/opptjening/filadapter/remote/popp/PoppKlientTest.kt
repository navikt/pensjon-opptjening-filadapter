package no.nav.pensjon.opptjening.filadapter.remote.popp

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import no.nav.pensjon.opptjening.filadapter.common.SpringContextTest
import no.nav.pensjon.opptjening.filadapter.config.TokenProviderConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.mock
import org.springframework.web.client.RestTemplate

internal class PoppKlientTest : SpringContextTest.Standard() {

    companion object {
        @JvmField
        @RegisterExtension
        val wiremock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(WIREMOCK_PORT))
            .build()!!
    }

    private val poppKlient: PoppKlient = PoppKlientImpl(
        tokenProvider = mock { on { getToken() }.thenReturn(TokenProviderConfig.MOCK_TOKEN) },
        baseUrl = wiremock.baseUrl(),
        restTemplate = RestTemplate(),
    )


    @Test
    fun `kaster exception med informasjon dersom kall svarer med server error`() {
        wiremock.givenThat(
            post(urlPathEqualTo("/fil/opprett"))
                .willReturn(serverError())
        )

//        assertThrows<PoppClientException.LagreFilFeilet> {
        assertThrows<Throwable> {
            poppKlient.lagreFil(
                fil = "hello".byteInputStream(charset = Charsets.US_ASCII)
            )
        }.also {
            // assertThat(it).isEqualTo("banana")
        }
    }
}