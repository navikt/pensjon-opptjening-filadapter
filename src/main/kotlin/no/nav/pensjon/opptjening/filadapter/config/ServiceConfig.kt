package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.domain.LagerstatusService
import no.nav.pensjon.opptjening.filadapter.domain.OverforNesteFilService
import no.nav.pensjon.opptjening.filadapter.domain.ProsesserFilService
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfig {

    @Bean
    fun prosesserFilService(
        filsluseKlient: FilsluseKlient,
        poppKlient: PoppKlient,
    ): ProsesserFilService {
        return ProsesserFilService(
            poppKlient = poppKlient,
            filsluseKlient = filsluseKlient,
            lagerstatusService = LagerstatusService(poppKlient)
        )
    }

    @Bean
    fun overforNesteFilService(
        filsluseKlient: FilsluseKlient,
        poppKlient: PoppKlient, lagerstatusService: LagerstatusService,
    ): OverforNesteFilService {
        return OverforNesteFilService(
            filsluseKlient = filsluseKlient,
            lagerstatusService = lagerstatusService,
        )
    }
}