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
    }

    override fun opprettFil(oppprettFilRequest: OpprettFilRequest): OpprettFilResponse {
        return callPopp(
            url = "$baseUrl/fil/opprett",
            body = oppprettFilRequest,
            responseType = OpprettFilResponse::class.java,
        )
    }

    override fun lagreFilSegment(lagreFilSegmentRequest: LagreFilSegmentRequest): LagreFilSegmentResponse {
        return callPopp(
            url = "$baseUrl/fil/leggtil",
            body = lagreFilSegmentRequest,
            responseType = LagreFilSegmentResponse::class.java,
        )
    }

    override fun validerFil(id: UUID): Boolean {
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
        val client = OkHttpClient.Builder()
            .build()
        val request = buildRequest(
            url = url,
            body = body,
        )
        return client.newCall(request).execute().use { response ->
            val body = response.body?.string()
            if (body.isNullOrEmpty()) {
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