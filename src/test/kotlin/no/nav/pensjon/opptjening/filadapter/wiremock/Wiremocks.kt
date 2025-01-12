package no.nav.pensjon.opptjening.filadapter.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.github.tomakehurst.wiremock.stubbing.StubMapping

fun WireMockExtension.`lagreFilMock`(): StubMapping {
    synchronized(this) {
        return this.stubFor(
            WireMock.post(WireMock.urlPathEqualTo("/popp/api/fil/opprett"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(202)
                        .withTransformerParameter("historiske", "banan")
                        .withBodyFile(
                            "popp/opprett.json"
                        )
                )
        )
    }
}

fun WireMockExtension.`validerFilMock`(): StubMapping {
    synchronized(this) {
        return this.stubFor(
            WireMock.post(WireMock.urlPathEqualTo("/popp/api/fil/opprett"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(202)
                        .withTransformerParameter("historiske", "banan")
                        .withBodyFile(
                            "popp/valider.json"
                        )
                )
        )
    }
}