package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import java.io.InputStream

interface FilsluseKlient {
    fun scanForFiles(remoteDir: String): List<RemoteFilInfo>
    fun downloadFile(fileName: String): InputStream
}
