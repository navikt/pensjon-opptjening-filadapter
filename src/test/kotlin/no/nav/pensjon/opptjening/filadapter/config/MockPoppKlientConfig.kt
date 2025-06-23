package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import org.mockito.kotlin.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class MockPoppKlientConfig {

    @Bean
    fun poppKlient(): PoppKlient {
        return mock()
    }

}