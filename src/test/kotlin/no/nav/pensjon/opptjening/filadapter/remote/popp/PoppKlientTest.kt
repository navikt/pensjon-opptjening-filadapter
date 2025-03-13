package no.nav.pensjon.opptjening.filadapter.remote.popp

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagerstatusResponse
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.LagreFilSegmentRequest
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilRequest
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.OpprettFilResponse
import no.nav.pensjon.opptjening.filadapter.utils.JsonUtils.toJson
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.*


internal class PoppKlientTest {

    companion object {
        val WIREMOCK_PORT = 15001

        @JvmField
        @RegisterExtension
        val wiremock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(WIREMOCK_PORT))
            .build()!!

        fun WireMockExtension.`opprettFilMock`(response: OpprettFilResponse): StubMapping {
            synchronized(this) {
                return this.stubFor(
                    post(urlPathEqualTo("/fil/opprett"))
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(200)
                                .withTransformerParameter("historiske", "banan")
                                .withBody(response.toJson())
                        )
                )
            }
        }
    }

    private val poppKlient: PoppKlient = PoppKlientImpl(
        baseUrl = wiremock.baseUrl(),
    ) { "token" }

    @Test
    fun `opprettFil kaster exception med informasjon dersom kall svarer med server error`() {
        wiremock.givenThat(
            post(urlPathEqualTo("/fil/opprett"))
                .willReturn(serverError())
        )
        assertThatThrownBy {
            poppKlient.opprettFil(
                OpprettFilRequest(
                    fileName = "foo.txt",
                    antallBytes = 93,
                )
            )
        }
            .isInstanceOf(PoppClientException.ResponseWithNoBody::class.java)
    }

    @Test
    fun `lagreSegment kaster exception med informasjon dersom kall svarer med server error`() {
        wiremock.givenThat(
            post(urlPathEqualTo("/fil/leggtil"))
                .willReturn(serverError())
        )
        assertThatThrownBy {
            poppKlient.lagreFilSegment(
                LagreFilSegmentRequest(
                    filId = UUID.randomUUID(),
                    segmentNo = 0,
                    data = "hello".toByteArray(charset = Charsets.US_ASCII)
                )
            )
        }
            .isInstanceOf(PoppClientException.ResponseWithNoBody::class.java)
    }

    @Test
    fun `valider kaster exception med informasjon dersom kall svarer med server error`() {
        wiremock.givenThat(
            post(urlPathEqualTo("/fil/valider"))
                .willReturn(serverError())
        )
        assertThatThrownBy {
            poppKlient.validerFil(UUID.randomUUID())
        }
            .isInstanceOf(PoppClientException.ResponseWithNoBody::class.java)
    }

    @Test
    fun `kan hente lagerstatus`() {
        val httpResponseBody = LagerstatusResponse(
            antallLagret = 2,
            antallLagres = 0,
        )

        wiremock.givenThat(
            post(urlPathEqualTo("/fil/lagerstatus"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withTransformerParameter("historiske", "banan")
                        .withBody(
                            httpResponseBody.toJson()
                        )
                )
        )
        val response = poppKlient.hentLagerstatus("hello.txt")
        assertThat(response).isEqualTo(httpResponseBody)
    }


    @Test
    fun `opprettFil som lykkes returnerer responsobjektet`() {
        val okResponse = OpprettFilResponse(
            filId = UUID.randomUUID(),
            informasjonsTekst = "Fil opprettet",
            status = OpprettFilResponse.Status.LAGRET_OK,
        )
        wiremock.`opprettFilMock`(okResponse)
        wiremock.givenThat(
            post(urlPathEqualTo("/fil/opprett"))
                .willReturn(serverError())
        )
        assertThatThrownBy {
            poppKlient.opprettFil(
                OpprettFilRequest(
                    fileName = "foo.txt",
                    antallBytes = 93,
                )
            )
        }
            .isInstanceOf(PoppClientException.ResponseWithNoBody::class.java)
    }
}