package no.nav.pensjon.opptjening.filadapter.repository

import no.nav.pensjon.opptjening.filadapter.common.SpringContextTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class FilInfoRepositoryTest(
    @Autowired val repository: FilInfoRepository
) : SpringContextTest.Standard() {

    @Test
    fun testRepo() {
        repository.lagreFilInfo(
            FilInfo(
                id = FilInfo.Id("hello.txt"),
                dato = LocalDateTime.now(),
                status = FilInfo.Status.NY,
            )
        )
        repository.hentFilInfo(FilInfo.Id("x"))
    }
}