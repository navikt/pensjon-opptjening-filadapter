package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

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
        println("FOO")
    }

}