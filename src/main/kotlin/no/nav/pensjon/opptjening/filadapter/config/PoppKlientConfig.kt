package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlient
import no.nav.pensjon.opptjening.filadapter.remote.popp.PoppKlientImpl
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pensjon.opptjening.azure.ad.client.TokenProvider

@Configuration
class PoppKlientConfig {
    @Bean
    fun poppKlient(
        @Value("\${POPP_URL}") baseUrl: String,
        @Qualifier("poppTokenProvider") poppTokenProvider: TokenProvider,
    ) : PoppKlient {
        return PoppKlientImpl(
            baseUrl = baseUrl,
            nextToken = { poppTokenProvider.getToken() }
        )
    }
}