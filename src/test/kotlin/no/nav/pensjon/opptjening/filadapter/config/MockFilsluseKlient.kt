package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.RemoteFilInfo
import java.io.ByteArrayInputStream
import java.io.InputStream

class MockFilsluseKlient : FilsluseKlient {
    override fun scanForFiles(remoteDir: String): List<RemoteFilInfo> {
        return listOf(
            RemoteFilInfo(
                name = "filnavn.txt",
                10,
            )
        )
    }

    override fun scanForFil(remoteDir: String, filnavn: String): RemoteFilInfo {
        return RemoteFilInfo(
            name = "filnavn.txt",
            10,
        )
    }

    override fun downloadFile(remoteDir: String, fileName: String): FilsluseKlient.FileDownload {
        return FileDownloadMock()
    }

    class FileDownloadMock: FilsluseKlient.FileDownload {
        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(ByteArray(0))
        }

        override fun close() {
        }

    }
}