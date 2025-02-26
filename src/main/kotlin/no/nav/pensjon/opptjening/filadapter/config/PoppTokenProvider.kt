package no.nav.pensjon.opptjening.filadapter.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import pensjon.opptjening.azure.ad.client.AzureAdConfig
import pensjon.opptjening.azure.ad.client.AzureAdTokenProvider
import pensjon.opptjening.azure.ad.client.AzureAdVariableConfig
import pensjon.opptjening.azure.ad.client.TokenProvider

@Configuration
@Profile("dev-fss", "prod-fss")
class PoppTokenProvider {
    @Bean
    fun tokenProvider(
        @Value("\${POPP_API_ID}") appId: String,
        azureAdConfig: AzureAdTokenClientConfig,
    ): TokenProvider {
        val config: AzureAdConfig = AzureAdVariableConfig(
            azureAppClientId = azureAdConfig.azureAppClientId,
            azureAppClientSecret = azureAdConfig.azureAppClientSecret,
            targetApiId = appId,
            wellKnownUrl = azureAdConfig.wellKnownUrl,
        )
        return AzureAdTokenProvider(config)
    }
}