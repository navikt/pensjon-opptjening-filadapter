package no.nav.pensjon.opptjening.filadapter.config

import io.getunleash.DefaultUnleash
import io.getunleash.Unleash
import io.getunleash.strategy.DefaultStrategy
import io.getunleash.util.UnleashConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.net.InetAddress

@Configuration
@Profile("dev-fss", "prod-fss")
class UnleashConfig(
    @Value("\${UNLEASH_SERVER_API_URL}") private val unleashUrl: String,
    @Value("\${UNLEASH_SERVER_API_TOKEN}") private val unleashApiKey: String
) {

    @Bean("unleash")
    fun unleashConfig(): Unleash {
        return DefaultUnleash(
            UnleashConfig.builder()
                .appName("pensjon-opptjening-filadapter")
                .instanceId(InetAddress.getLocalHost().hostName)
                .unleashAPI("$unleashUrl/api")
                .apiKey(unleashApiKey)
                .build(),
            DefaultStrategy()
        )
    }

    enum class Feature(val toggleName: String) {
//      IKKE_IMPLEMENTERT("pensjon-opptjening-filadapter-antall-lagret-i-popp),
    }
}

