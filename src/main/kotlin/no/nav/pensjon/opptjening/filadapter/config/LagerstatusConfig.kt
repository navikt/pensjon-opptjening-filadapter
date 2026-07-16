package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.domain.LagerstatusService
import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Configuration
class LagerstatusConfig {
    @Bean
    fun lagerstatusService(
        poppKlient: PoppKlient,
        lagerstatusExecutor: ExecutorService,
    ): LagerstatusService {
        return LagerstatusService(
            poppKlient = poppKlient,
            lagerstatusExecutor = lagerstatusExecutor,
        )
    }

    /**
     * Begrenset trådpool for parallelle POPP-lagerstatus-oppslag i /list.
     * 8 tråder gir god parallellitet uten å oversvømme POPP.
     *
     * Trådene navngis «lagerstatus-N» så de er lette å kjenne igjen i trådump og metrikker.
     * destroyMethod = "shutdown" gjør nedstengninga eksplisitt (Spring utleder den også, men
     * her sier vi det tydelig).
     */
    @Bean(destroyMethod = "shutdown")
    fun lagerstatusExecutor(): ExecutorService {
        val teller = AtomicInteger(1)
        return Executors.newFixedThreadPool(8) { oppgave ->
            Thread(oppgave, "lagerstatus-${teller.getAndIncrement()}")
        }
    }
}
