package no.nav.pensjon.opptjening.filadapter.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class ProsesserFilServiceTest {

    @Test
    fun `kan lagre en fil`() {
        val poppKlient = TestPoppKlient()
        val service = ProsesserFilService(poppKlient)
        val fil = this.javaClass.getResource("/testfil.txt")!!.toURI().let { Path.of(it) }
        val id = service.prosesser(fil)
        assertThat(id).isNotNull
        val innhold = poppKlient.hentInnhold(id!!)
        assertThat(Files.readAllBytes(fil)).isEqualTo(innhold)
    }

}