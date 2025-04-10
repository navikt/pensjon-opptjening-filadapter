package no.nav.pensjon.opptjening.filadapter.web

import no.nav.pensjon.opptjening.filadapter.domain.LagerstatusService
import no.nav.pensjon.opptjening.filadapter.domain.ProsesserFilService
import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.web.dto.ListFilerResponse
import no.nav.pensjon.opptjening.filadapter.web.dto.OverforFilRequest
import no.nav.pensjon.opptjening.filadapter.web.dto.OverforFilResponse
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@Protected
class AdminWebApi(
    val filsluseKlient: FilsluseKlient,
    val prosesserFilService: ProsesserFilService,
    val lagerstatusService: LagerstatusService,
) {
    companion object {
        private val log = NAVLog(AdminWebApi::class)
    }

    init {
        println("initializing AdminWebApi")
    }

    @GetMapping("/list")
    fun listFiler(): ResponseEntity<ListFilerResponse> {
        log.open.info("List filer")
        return filsluseKlient.scanForFiles("/outbound").map {
            val filnavn = it.name
            val size = it.size
            val erlagret = lagerstatusService.erLagret(filnavn)
            ListFilerResponse.FilMedStatus(
                filnavn = filnavn,
                size = size,
                lagret = erlagret,
            )
        }
            .let { ListFilerResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @PostMapping("/overfor")
    fun overforFil(
        @RequestBody request: OverforFilRequest,
    ): ResponseEntity<OverforFilResponse> {
        val resultat = prosesserFilService.overførFil(
            dir = "outbound",
            filnavn = request.filnavn,
            blockSize = 100_000
        )
        return when (resultat.status) {

            ProsesserFilService.OverførResultat.Status.OK -> {
                ResponseEntity.ok(OverforFilResponse.ok(resultat.filId!!))
            }

            ProsesserFilService.OverførResultat.Status.FINNES_IKKE_I_FILSLUSE -> {
                ResponseEntity.internalServerError()
                    .body(OverforFilResponse.feilet(null, "Finnes ikke i filsluse: ${resultat.filnavn}"))
            }

            ProsesserFilService.OverførResultat.Status.FEILET -> {
                ResponseEntity.internalServerError()
                    .body(OverforFilResponse.feilet(resultat.filId, "Overføring feilet: ${resultat.filnavn}"))
            }
        }
    }

    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> {
        log.open.info("Sa hei")
        return ResponseEntity.ok("pong")
    }
}