package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.RemoteFilInfo

class FinnFilerService(
    val filsluseKlient: FilsluseKlient
) {
    companion object {
        private val log = NAVLog(FinnFilerService::class)
    }

    fun hentFiler() {
        val filer = filsluseKlient.scanForFiles("/")
        filer.forEach {
            registrerFilsluseFil(it)
        }
    }

    fun registrerFilsluseFil(fil: RemoteFilInfo) {
        log.secure.info("registrerFilsluseFil: $fil")
    }
}