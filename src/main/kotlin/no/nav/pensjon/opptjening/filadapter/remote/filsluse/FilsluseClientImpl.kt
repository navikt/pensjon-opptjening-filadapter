package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import com.jcraft.jsch.JSch

class FilsluseClientImpl(
) : FilsluseClient {
    override fun scanForFiles(): List<RemoteFilInfo> {
        val jsch = JSch()
        TODO("Not yet implemented")
    }
}