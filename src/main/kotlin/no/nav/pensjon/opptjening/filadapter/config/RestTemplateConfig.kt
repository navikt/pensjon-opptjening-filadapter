package no.nav.pensjon.opptjening.filadapter.config

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {

    @Bean
    fun restTemplate(
        clientHttpRequestFactory: ClientHttpRequestFactory
    ): RestTemplate {
        return RestTemplate(clientHttpRequestFactory)
    }

    @Bean
    fun clientHttpRequestFactory(
        httpClientConnectionManager: org.apache.hc.client5.http.io.HttpClientConnectionManager
    ): ClientHttpRequestFactory {
        return HttpComponentsClientHttpRequestFactory().apply {
            httpClient = org.apache.hc.client5.http.impl.classic.HttpClients.custom()
                .setDefaultRequestConfig(org.apache.hc.client5.http.config.RequestConfig.DEFAULT)
                .setConnectionManager(httpClientConnectionManager)
                .build()
        }
    }

    @Bean
    fun poolingConnectionManager(
        registry: MeterRegistry
    ): org.apache.hc.client5.http.io.HttpClientConnectionManager {
        return org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager().apply {
            defaultMaxPerRoute = 12
            maxTotal = 64
            setDefaultConnectionConfig(
                org.apache.hc.client5.http.config.ConnectionConfig.custom()
                    .setConnectTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(30))
                    .setSocketTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(30))
                    .setTimeToLive(org.apache.hc.core5.util.TimeValue.ofSeconds(60))
                    .setValidateAfterInactivity(org.apache.hc.core5.util.TimeValue.ofSeconds(10))
                    .build()
            )

            CustomPoolingHttpClientConnectionManagerMetrics(
                this,
                registry
            )
        }
    }
}