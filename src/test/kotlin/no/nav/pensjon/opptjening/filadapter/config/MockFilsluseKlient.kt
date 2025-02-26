package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.RemoteFilInfo
import java.io.ByteArrayInputStream
import java.io.InputStream

class MockFilsluseKlient : FilsluseKlient {
    override fun scanForFiles(remoteDir: String): List<RemoteFilInfo> {
        return listOf(
            RemoteFilInfo(
                name = "filnavn.txt"
            )
        )
    }

    override fun downloadFile(fileName: String): InputStream {
        return ByteArrayInputStream(ByteArray(0))
    }
}