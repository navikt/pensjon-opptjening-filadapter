package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlientImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.lang.Integer.parseInt

@Profile("dev-gcp", "prod-gcp")
@Configuration
class FilsluseConfig {
    @Bean
    fun filsluseKlient(
        @Value("\${FILSLUSE_HOST}") host: String,
        @Value("\${FILSLUSE_PORT}") port: String,
        @Value("\${FILSLUSE_USERNAME}") username: String,
        @Value("\${SSH_PRIVATE_KEY}") privateKey: String,
        @Value("\${SSH_PUBLIC_KEY}") publicKey: String,
        @Value("\${SSH_PRIVATE_KEY_PASSPHRASE}") passphrase: String,
    ): FilsluseKlient {
        return FilsluseKlientImpl(
            host = host,
            port = parseInt(port),
            username = username,
            privateKey = privateKey,
            publicKey = publicKey,
            privateKeyPassword = passphrase
        )
    }
}