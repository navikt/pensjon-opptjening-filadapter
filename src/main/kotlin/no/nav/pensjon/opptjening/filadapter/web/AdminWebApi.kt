package no.nav.pensjon.opptjening.filadapter.web

import no.nav.pensjon.opptjening.filadapter.domain.ProsesserFilService
import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
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
) {
    companion object {
        private val log = NAVLog(AdminWebApi::class)
    }

    init {
        println("initializing AdminWebApi")
    }

    @GetMapping("/list")
    fun listFiler(): ResponseEntity<String> {
        log.open.info("List filer")
        return filsluseKlient.scanForFiles("/inbound")
            .map {
                it.name
            }
            .joinToString("\n")
            .let { ResponseEntity.ok(it) }
    }

    @PostMapping("/overfor")
    fun overforFil(
        @RequestBody request: OverforFilRequest,
    ): ResponseEntity<OverforFilResponse> {
        // prosesserFilService.prosesser(request.filnavn)
        return ResponseEntity.ok(OverforFilResponse.feilet("x"))
    }

    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> {
        log.open.info("Sa hei")
        return ResponseEntity.ok("pong")
    }
}