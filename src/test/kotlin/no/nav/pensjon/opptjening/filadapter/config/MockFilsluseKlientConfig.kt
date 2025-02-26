package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.RemoteFilInfo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.io.InputStream

@Configuration
class MockFilsluseKlientConfig {
    @Bean
    fun filsluseKlient(): FilsluseKlient {
        return MockFilsluseKlient()
    }
}