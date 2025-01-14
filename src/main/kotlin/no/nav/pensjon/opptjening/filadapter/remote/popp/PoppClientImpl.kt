package no.nav.pensjon.opptjening.filadapter.remote.popp

import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import no.nav.pensjon.opptjening.filadapter.utils.JsonUtils.mapToObject
import no.nav.pensjon.opptjening.filadapter.utils.JsonUtils.toJson
import no.nav.popp.web.api.endpoint.fil.model.LagreFilRequest
import no.nav.popp.web.api.endpoint.fil.model.LagreFilResponse
import no.nav.popp.web.api.endpoint.fil.model.ValiderFilRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import pensjon.opptjening.azure.ad.client.TokenProvider
import java.io.InputStream
import java.util.*

@Component
class PoppClientImpl(
    @Qualifier("poppTokenProvider") private val tokenProvider: TokenProvider,
    @Value("\${POPP_URL}") private val baseUrl: String,
//    private val metrikker: Metrikker,
    private val restTemplate: RestTemplate,
) : PoppClient {

    companion object {
        private val log = NAVLog(PoppClientImpl::class)
    }

//    val webClient: WebClient = webClientBuilder.baseUrl("http://localhost:9991/xxxx").build()

    override fun lagreFil(fil: InputStream): UUID {
        log.open.info("Lagrer fil")
        val innhold = fil.readAllBytes()
        val lagreRequest = LagreFilRequest(
            fileName = "TODO",
            tegnsett = LagreFilRequest.Tegnsett.US_ASCII,
            antallBytes = innhold.size.toLong(),
            data = innhold
        )
        val response = lagreFil(lagreRequest)
        return response.filId!!
    }

    fun lagreFil(request: LagreFilRequest): LagreFilResponse {
        val url = "$baseUrl/fil/opprett"
        val response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            HttpEntity(
                request.toJson(),
                HttpHeaders().apply {
                    accept = listOf(MediaType.APPLICATION_JSON)
                    contentType = MediaType.APPLICATION_JSON
                    setBearerAuth(tokenProvider.getToken())
                }
            ),
            String::class.java
        )

        try {
            return response.body
                ?.mapToObject(LagreFilResponse::class.java)
                ?: throw PoppClientException.LagreFilFeilet("Response var null", null)
        } catch (ex: Throwable) {
            log.open.error("Lagring av fil feilet")
            log.secure.error("Lagring av fil feilet", ex)
            throw PoppClientException.LagreFilFeilet("Kunne ikke deserialisere svar", ex)
        }
    }

    override fun validerFil(id: UUID): Boolean {
        val validerFilRequest = ValiderFilRequest(
            id = id
        )
        return false
    }
}