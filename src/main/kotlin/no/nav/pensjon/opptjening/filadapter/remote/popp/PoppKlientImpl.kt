package no.nav.pensjon.opptjening.filadapter.remote.popp

import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import no.nav.pensjon.opptjening.filadapter.remote.popp.domain.*
import no.nav.pensjon.opptjening.filadapter.utils.JsonUtils.mapToObject
import no.nav.pensjon.opptjening.filadapter.utils.JsonUtils.toJson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class PoppKlientImpl(
    private val baseUrl: String,
    private val nextToken: () -> String,
) : PoppKlient {

    companion object {
        private val log = NAVLog(PoppKlientImpl::class)
        val client = OkHttpClient.Builder().build()
    }

    override fun opprettFil(request: OpprettFilRequest): OpprettFilResponse {
        log.open.info("Oppretter fil med navn ${request.fileName} i POPP")
        return callPopp(
            url = "$baseUrl/fil/opprett",
            body = request,
            responseType = OpprettFilResponse::class.java,
        )
    }

    override fun lagreFilSegment(lagreFilSegmentRequest: LagreFilSegmentRequest): LagreFilSegmentResponse {
        log.open.info("Lagrer filsegment for filId ${lagreFilSegmentRequest.filId} i POPP")
        return callPopp(
            url = "$baseUrl/fil/leggtil",
            body = lagreFilSegmentRequest,
            responseType = LagreFilSegmentResponse::class.java,
        )
    }

    override fun hentLagerstatus(filnavn: String): LagerstatusResponse {
        log.open.info("Henter lagerStatus for $filnavn fra $baseUrl/fil/lagerstatus")
        return callPopp(
            url = "$baseUrl/fil/lagerstatus",
            body = LagerstatusRequest(filnavn),
            responseType = LagerstatusResponse::class.java,
        )
    }

    override fun validerFil(id: UUID): Boolean {
        log.open.info("Validerer fil med id $id i POPP")
        val response = callPopp(
            url = "$baseUrl/fil/valider",
            body = ValiderFilRequest(id),
            responseType = ValiderFilResponse::class.java,
        )
        return response.status == ValiderFilResponse.Status.OK
    }


    private fun buildRequest(url: String, body: Any): Request {
        return Request.Builder()
            .post(body.toJson().toRequestBody("application/json".toMediaTypeOrNull()!!))
            .url(url)
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer ${nextToken()}")
            .build()
    }


    private fun <T : Any> callPopp(
        url: String,
        body: Any,
        responseType: Class<T>
    ): T {
        val request = buildRequest(
            url = url,
            body = body,
        )
        return client.newCall(request).execute().use { response ->
            val body = response.body.string()
            if (body.isEmpty()) {
                throw PoppClientException.ResponseWithNoBody(response.code)
            }
            val callResponse =
                try {
                    body.mapToObject(responseType)
                } catch (t: Throwable) {
                    log.open.error("Kunne ikke lese svar fra popp")
                    log.secure.error("Kunne ikke lese svar fra popp", t)
                    log.secure.error("response ${response.code} ${response.message}")
                    log.secure.error("body: $body")
                    throw PoppClientException.FailedToParseResponse("Kunne ikke lese svar fra popp", t)
                }
            if (response.code != 200) {
                throw PoppClientException.KallFeilet(
                    message = "response status code ${response.code}",
                    response = callResponse
                )
            }
            callResponse
        }
    }
}