package no.nav.pensjon.opptjening.filadapter.repository

import no.nav.pensjon.opptjening.filadapter.Application
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest(classes = [Application::class])
class FilInfoRepositoryTest(
    @Autowired val repository: FilInfoRepository
) {

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