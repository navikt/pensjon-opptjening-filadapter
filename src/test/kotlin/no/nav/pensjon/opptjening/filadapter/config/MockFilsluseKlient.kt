package no.nav.pensjon.opptjening.filadapter.config

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.Instant
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.RemoteFilInfo

class MockFilsluseKlient : FilsluseKlient {
    companion object {
        val FAST_TIDSPUNKT: Instant = Instant.parse("2025-03-15T10:30:00Z")
    }

    override fun scanForFiles(remoteDir: String): List<RemoteFilInfo> {
        return listOf(
            RemoteFilInfo(
                name = "filnavn.txt",
                size = 10,
                modifiedAt = FAST_TIDSPUNKT,
            )
        )
    }

    override fun scanForFil(remoteDir: String, filnavn: String): RemoteFilInfo {
        return RemoteFilInfo(
            name = "filnavn.txt",
            size = 10,
            modifiedAt = FAST_TIDSPUNKT,
        )
    }

    override fun downloadFile(remoteDir: String, fileName: String): FilsluseKlient.FileDownload {
        return FileDownloadMock()
    }

    class FileDownloadMock : FilsluseKlient.FileDownload {
        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(ByteArray(0))
        }

        override fun close() {
        }

    }
}