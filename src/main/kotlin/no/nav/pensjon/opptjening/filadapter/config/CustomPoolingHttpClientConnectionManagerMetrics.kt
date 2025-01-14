package no.nav.pensjon.opptjening.filadapter.config

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.httpcomponents.hc5.PoolingHttpClientConnectionManagerMetricsBinder
import org.apache.hc.client5.http.HttpRoute
import org.apache.hc.core5.pool.ConnPoolControl

class CustomPoolingHttpClientConnectionManagerMetrics(
    private val pool: ConnPoolControl<HttpRoute?>,
    private val registry: MeterRegistry,
) {
    init {
        registerTotalMetrics()
        registerPerRouteMetrics()
    }

    /**
     * Copied from [PoolingHttpClientConnectionManagerMetricsBinder]
     */
    private fun registerTotalMetrics() {
        Gauge.builder(
            "httpcomponents.httpclient.pool.total.max",
            pool
        ) { connPoolControl: ConnPoolControl<HttpRoute?> ->
            connPoolControl.totalStats
                .max
                .toDouble()
        }
            .description("The configured maximum number of allowed persistent connections for all routes.")
            .register(registry)

        Gauge.builder(
            "httpcomponents.httpclient.pool.total.connections",
            pool
        ) { connPoolControl: ConnPoolControl<HttpRoute?> ->
            connPoolControl.totalStats
                .available
                .toDouble()
        }
            .description("The number of persistent and available connections for all routes.")
            .tag("state", "available")
            .register(registry)

        Gauge.builder(
            "httpcomponents.httpclient.pool.total.connections",
            pool
        ) { connPoolControl: ConnPoolControl<HttpRoute?> ->
            connPoolControl.totalStats
                .leased
                .toDouble()
        }
            .description("The number of persistent and leased connections for all routes.")
            .tag("state", "leased")
            .register(registry)

        Gauge.builder(
            "httpcomponents.httpclient.pool.total.pending",
            pool
        ) { connPoolControl: ConnPoolControl<HttpRoute?> ->
            connPoolControl.totalStats
                .pending
                .toDouble()
        }
            .description("The number of connection requests being blocked awaiting a free connection for all routes.")
            .register(registry)

        Gauge.builder(
            "httpcomponents.httpclient.pool.route.max.default",
            pool
        ) { obj: ConnPoolControl<HttpRoute?> ->
            obj.defaultMaxPerRoute
                .toDouble()
        }
            .description("The configured default maximum number of allowed persistent connections per route.")
            .register(registry)
    }

    private fun registerPerRouteMetrics() {
        Gauge.builder(
            "httpcomponents.httpclient.pool.route.initializer",
            pool
        ) { connPoolControl: ConnPoolControl<HttpRoute?> ->

            pool.routes.mapNotNull { route ->
                Gauge.builder(
                    "httpcomponents.httpclient.pool.route.available",
                    pool
                ) { connPoolControl: ConnPoolControl<HttpRoute?> ->
                    connPoolControl.getStats(route)
                        .available
                        .toDouble()
                }
                    .description("The number of persistent and available connections for per route.")
                    .tag("state", "available")
                    .tag("route", route.toString())
                    .register(registry)

                Gauge.builder(
                    "httpcomponents.httpclient.pool.route.leased",
                    pool
                ) { connPoolControl: ConnPoolControl<HttpRoute?> ->
                    connPoolControl.getStats(route)
                        .leased
                        .toDouble()
                }
                    .description("The number of persistent and leased connections per route.")
                    .tag("state", "leased")
                    .tag("route", route.toString())
                    .register(registry)

                Gauge.builder(
                    "httpcomponents.httpclient.pool.route.pending",
                    pool
                ) { connPoolControl: ConnPoolControl<HttpRoute?> ->
                    connPoolControl.getStats(route)
                        .pending
                        .toDouble()
                }
                    .description("The number of connection requests being blocked awaiting a free connection per route.")
                    .tag("state", "pending")
                    .tag("route", route.toString())
                    .register(registry)
            }

            connPoolControl.routes.count().toDouble()
        }
            .description("Initialize stats for routes that does noe exist on startup")
            .tag("state", "initialized")
            .register(registry)
    }
}