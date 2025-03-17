package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.domain.LagerstatusService
import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LagerstatusConfig {
    @Bean
    fun lagerstatusService(
        poppKlient: PoppKlient,
    ): LagerstatusService {
        return LagerstatusService(
            poppKlient = poppKlient
        )
    }
}