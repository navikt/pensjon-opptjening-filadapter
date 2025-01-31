package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.RemoteFilInfo

class FinnFilerService(
    val filsluseKlient: FilsluseKlient
) {
    fun hentFiler() {
        val filer = filsluseKlient.scanForFiles("/")
        filer.forEach {
            registrerFilsluseFil(it)
        }
    }

    fun registrerFilsluseFil(fil: RemoteFilInfo) {
        println("Registrerer: $fil")
    }
}