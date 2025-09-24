package no.nav.pensjon.opptjening.filadapter.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("dev-fss", "prod-fss")
class AzureAdTokenClientConfig(
    @param:Value("\${AZURE_APP_CLIENT_ID}") val azureAppClientId: String,
    @param:Value("\${AZURE_APP_CLIENT_SECRET}") val azureAppClientSecret: String,
    @param:Value("\${AZURE_APP_WELL_KNOWN_URL}") val wellKnownUrl: String,
)