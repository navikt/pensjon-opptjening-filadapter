package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class FilsluseClientTest {

    companion object {

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
    fun test1() {
        println("port: ${sftpServer.getPort()}")
        val remoteFiles = FilsluseClientImpl(
            host = "127.0.0.1",
            port = sftpServer.getPort(),
            username = "test",
            privateKeyPath = Paths.get(
                this::class.java.getResource("/test_id_client_rsa")!!.toURI()
            ),
        ).scanForFiles("/")
        println("remoteFiles: $remoteFiles")
        assertThat(remoteFiles).contains(RemoteFilInfo("testfile.txt"))
    }
}