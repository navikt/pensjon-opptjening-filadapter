package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseClient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseClientImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.lang.Integer.parseInt

@Profile("dev-gcp", "prod-gcp")
@Configuration
class FilsluseConfig {
    @Bean
    fun filsluseClient(
        @Value("\${FILSLUSE_HOST}") host: String,
        @Value("\${FILSLUSE_PORT}") port: String,
        @Value("\${FILSLUSE_USERNAME}") username: String,
        @Value("\${SSH_PRIVATE_KEY}") privateKey: String,
        @Value("\${SSH_PRIVATE_KEY_PASSPHRASE}") passphrase: String,
    ): FilsluseClient {
        return FilsluseClientImpl(
            host = host,
            port = parseInt(port),
            username = username,
            privateKey = privateKey,
            privateKeyPassword = passphrase
        )
    }
}