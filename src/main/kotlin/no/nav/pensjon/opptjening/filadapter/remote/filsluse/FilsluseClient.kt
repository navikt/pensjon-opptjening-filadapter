package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import java.io.InputStream

interface FilsluseClient {
    fun scanForFiles(remoteDir: String): List<RemoteFilInfo>
    fun downloadFile(fileName: String): InputStream
}
