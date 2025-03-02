package no.nav.pensjon.opptjening.filadapter.domain

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlientImpl
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.LocalSftpServer
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.TestSftpConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class ProsesserFilServiceTest {

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
    fun `kan lagre en fil`() {
        val poppKlient = TestPoppKlient()
        val service = ProsesserFilService(poppKlient, filsluseKlient = sftpClient(sftpServer))
        val fil = this.javaClass.getResource("/testfil.txt")!!.toURI().let { Path.of(it) }
        val id = service.prosesser(fil, 8)
        assertThat(id).isNotNull
        val innhold = poppKlient.hentInnhold(id)
        assertThat(Files.readAllBytes(fil)).isEqualTo(innhold)
    }

    @Test
    fun `kan overføre en fil`() {
        val poppKlient = TestPoppKlient()
        val service = ProsesserFilService(poppKlient, filsluseKlient = sftpClient(sftpServer))
        val fil = this.javaClass.getResource("/testfil.txt")!!.toURI().let { Path.of(it) }
        val resultat = service.overførFil(".", "testfile.txt", blockSize = 8)
        assertThat(resultat.status).isEqualTo(ProsesserFilService.OverførResultat.Status.OK)
    }

    @Test
    fun `returner FINNES_IKKE_I_FILSLUSE dersom filen ikke finnes i filsluse`() {
        val poppKlient = TestPoppKlient()
        val service = ProsesserFilService(poppKlient, filsluseKlient = sftpClient(sftpServer))
        val resultat = service.overførFil(".", "finnesikke.txt", blockSize = 8)
        assertThat(resultat.status).isEqualTo(ProsesserFilService.OverførResultat.Status.FINNES_IKKE_I_FILSLUSE)
    }

    private fun sftpClient(sftpServer: LocalSftpServer): FilsluseKlient {
        return FilsluseKlientImpl(
            host = "127.0.0.1",
            port = sftpServer.getPort(),
            username = "test",
            privateKey = TestSftpConfig.clientPrivate,
            publicKey = TestSftpConfig.clientPublic,
            privateKeyPassword = "password",
        )
    }
}