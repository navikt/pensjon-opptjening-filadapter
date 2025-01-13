package no.nav.pensjon.opptjening.filadapter.remote.popp

import no.nav.popp.web.api.endpoint.fil.model.LagreFilRequest
import no.nav.popp.web.api.endpoint.fil.model.ValiderFilRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import pensjon.opptjening.azure.ad.client.TokenProvider
import java.io.InputStream
import java.util.*

@Component
class PoppClientImpl(
    @Qualifier("barnetrygdTokenProvider") private val tokenProvider: TokenProvider,
    @Value("\${POPP_FIL_URL}") private val url: String,
//    private val metrikker: Metrikker,
    webClientBuilder: WebClient.Builder,
) : PoppClient {
    val webClient: WebClient = webClientBuilder.baseUrl("http://localhost:9991/xxxx").build()

    override fun lagreFil(fil: InputStream): UUID {
        val innhold = fil.readAllBytes()
        val lagreRequest = LagreFilRequest(
            fileName = "TODO",
            tegnsett = LagreFilRequest.Tegnsett.US_ASCII,
            antallBytes = innhold.size.toLong(),
            data = innhold
        )
        return UUID.randomUUID()
    }

    override fun validerFil(id: UUID): Boolean {
        val validerFilRequest = ValiderFilRequest(
            id = id
        )
        return false
    }
}