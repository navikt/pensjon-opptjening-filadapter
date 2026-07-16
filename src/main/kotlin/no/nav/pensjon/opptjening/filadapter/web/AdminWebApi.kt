package no.nav.pensjon.opptjening.filadapter.web

import no.nav.pensjon.opptjening.filadapter.domain.LagerstatusService
import no.nav.pensjon.opptjening.filadapter.domain.ProsesserFilService
import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.RemoteFilInfo
import no.nav.pensjon.opptjening.filadapter.web.dto.ListFilerResponse
import no.nav.pensjon.opptjening.filadapter.web.dto.OverforFilRequest
import no.nav.pensjon.opptjening.filadapter.web.dto.OverforFilResponse
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Protected
class AdminWebApi(
    val filsluseKlient: FilsluseKlient,
    val prosesserFilService: ProsesserFilService,
    val lagerstatusService: LagerstatusService,
) {
    companion object {
        private val log = NAVLog(AdminWebApi::class)
        private const val MAKS_ANTALL = 200
        private const val STANDARD_ANTALL = 50
    }

    init {
        println("initializing AdminWebApi")
    }

    /**
     * Lister filer fra filsluse, sortert nyeste først (modifiedAt synkende).
     *
     * Paginert: frontend henter én side om gangen og bygger opp lista gradvis. Hver fil krever
     * et POPP-lagerstatus-oppslag, så vi henter status kun for filene på den etterspurte sida,
     * og gjør oppslagene parallelt (LagerstatusService.lagerStatusForFiler). Dette holder
     * responstida lav uansett hvor mange filer som ligger i filsluse.
     */
    @GetMapping("/list")
    fun listFiler(
        @RequestParam(defaultValue = "0") side: Int,
        @RequestParam(defaultValue = "$STANDARD_ANTALL") antall: Int,
    ): ResponseEntity<ListFilerResponse> {
        val gyldigSide = side.coerceAtLeast(0)
        val gyldigAntall = antall.coerceIn(1, MAKS_ANTALL)
        log.open.info("List filer (side=$gyldigSide, antall=$gyldigAntall)")

        val alleFiler = filsluseKlient.scanForFiles("/outbound").sortedWith(compareByDescending<RemoteFilInfo> { it.modifiedAt }.thenBy { it.name })
        val totalt = alleFiler.size

        val sideFiler = alleFiler.drop(gyldigSide * gyldigAntall).take(gyldigAntall)
        val statuser = lagerstatusService.lagerStatusForFiler(sideFiler.map { it.name })

        val filer = sideFiler.zip(statuser) { fil, lagerStatus ->
            ListFilerResponse.FilMedStatus(
                filnavn = fil.name,
                size = fil.size,
                lagretMedId = lagerStatus.klarMedId,
                lagresMedId = lagerStatus.uferdigeMedId,
                modifiedAt = fil.modifiedAt,
                prosessert = lagerStatus.prosessert,
            )
        }
        val harMer = (gyldigSide + 1) * gyldigAntall < totalt

        return ResponseEntity.ok(
            ListFilerResponse(
                filer = filer,
                side = gyldigSide,
                antall = gyldigAntall,
                totalt = totalt,
                harMer = harMer,
            )
        )
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
                ResponseEntity.badRequest()
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