package no.nav.pensjon.opptjening.filadapter.config

import no.nav.security.mock.oauth2.MockOAuth2Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// @Configuration
class OAuthServerConfig {

    // @Bean
    fun mockOAuth2Server() : MockOAuth2Server {
        val server = MockOAuth2Server()
        server.start()
        println("issuerUrl: "+server.issuerUrl("azure"))
        return server
    }
}