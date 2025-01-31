package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.io.path.readBytes

class FilsluseKlientTest {

    companion object {

        const val sshClientPrivateKey = ""
        val sshClientPassword = null

        @JvmStatic
        val sftpServer = LocalSftpServer.default()

        @JvmStatic
        @BeforeAll
        fun startServer() {
            println("Starting sftp server...")
            sftpServer.start()
        }

        @JvmStatic
        @AfterAll
        fun stopServer() {
            println("Stopping sftp server...")
            sftpServer.stop()
        }
    }

    @Test
    fun `kan liste filer på sftp-serveren`() {
        println("port: ${sftpServer.getPort()}")
        val remoteFiles = sftpClient(sftpServer).scanForFiles("/")
        println("remoteFiles: $remoteFiles")
        assertThat(remoteFiles).contains(RemoteFilInfo("testfile.txt"))
    }

    @Test
    fun `kan laste ned en fil som finnes`() {
        val forventetInnhold = TestSftpConfig.sftpFilePath.resolve("testfile.txt").readBytes()
        println("port: ${sftpServer.getPort()}")
        val file = sftpClient(sftpServer).downloadFile("testfile.txt")
        assertThat(file).isNotNull()
        val nedlastetFil = file.readBytes()
        assertThat(nedlastetFil).isEqualTo(forventetInnhold)
    }

    @Test
    fun `feiler ved nedlasting av fil som ikke finnes`() {
        assertThatThrownBy {
            val file = sftpClient(sftpServer).downloadFile("banan.txt")
        }
            .isInstanceOf(FilsluseKlientImpl.SftpClientException.NoSuchFileOrDirectory::class.java)
    }


    private fun sftpClient(sftpServer: LocalSftpServer): FilsluseKlient {
        return FilsluseKlientImpl(
            host = "127.0.0.1",
            port = sftpServer.getPort(),
            username = "test",
            privateKey = TestSftpConfig.clientPrivate,
            privateKeyPassword = "password",
        )
    }
}