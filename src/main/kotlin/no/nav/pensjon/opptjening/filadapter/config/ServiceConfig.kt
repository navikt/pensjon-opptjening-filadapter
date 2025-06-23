package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.domain.LagerstatusService
import no.nav.pensjon.opptjening.filadapter.domain.OverforNesteFil
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
        )
    }

    @Bean
    fun overforNesteFilService(
        filsluseKlient: FilsluseKlient,
        lagerstatusService: LagerstatusService,
        prosesserFilService: ProsesserFilService,
    ): OverforNesteFil {
        return OverforNesteFil(
            filsluseKlient = filsluseKlient,
            lagerstatusService = lagerstatusService,
            prosesserFilService = prosesserFilService,
        )
    }
}