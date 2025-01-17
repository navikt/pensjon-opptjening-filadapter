package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import java.nio.file.Path

interface FilsluseClient {
    fun scanForFiles(remoteDir: String): List<RemoteFilInfo>
}