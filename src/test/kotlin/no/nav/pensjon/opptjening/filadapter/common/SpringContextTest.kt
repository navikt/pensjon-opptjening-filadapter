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
        const val WIREMOCK_PORT = 9991
        const val POPP_URL="http://localhost:9991/popp/api"
    }

    /*
    companion object {
        const val PDL_PATH = "/graphql"
        const val BESTEM_SAK_PATH = "/pen/api/bestemsak/v1"
        const val OPPGAVE_PATH = "/api/v1/oppgaver"
        const val POPP_PENSJONSPOENG_PATH = "/api/pensjonspoeng"
        const val MEDLEMSKAP_PATH = "/api/v1/medlemskapsunntak"
        const val PEN_ALDERVEDTAK_PATH = "/pen/api/alderspensjon/vedtak/gjeldende"
        const val PEN_UFOREVEDTAK_PATH = "/pen/api/uforetrygd/vedtak/gjeldende"
    }
     */

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