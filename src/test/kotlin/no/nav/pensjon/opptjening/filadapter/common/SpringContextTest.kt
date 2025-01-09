package no.nav.pensjon.opptjening.filadapter.common

import jakarta.annotation.PostConstruct
import no.nav.pensjon.opptjening.filadapter.Application
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext
@EnableMockOAuth2Server
sealed class SpringContextTest {

    companion object {
    }

    /**
     * BEWARE: This instance is shared among tests when autowired.
     */
    @BeforeEach
    protected open fun beforeEach() {
        PostgresqlTestContainer.instance.removeDataFromDB()
    }

    @SpringBootTest(classes = [Application::class])
    class Standard : SpringContextTest() {

        @PostConstruct
        fun postConstruct() {
        }
    }
}