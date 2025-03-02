package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import java.io.InputStream

interface FilsluseKlient {
    fun scanForFiles(remoteDir: String): List<RemoteFilInfo>
    fun scanForFil(remoteDir: String, filnavn: String): RemoteFilInfo?
    fun downloadFile(remoteDir: String, fileName: String): InputStream
}
