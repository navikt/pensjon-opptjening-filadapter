package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MockFilsluseKlientConfig {
    @Bean
    fun filsluseKlient(): FilsluseKlient {
        return MockFilsluseKlient()
    }
}