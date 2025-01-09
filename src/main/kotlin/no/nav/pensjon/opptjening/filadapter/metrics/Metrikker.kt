package no.nav.pensjon.opptjening.filadapter.metrics

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class Metrikker(private val registry: MeterRegistry) {
    private val feiledeFiler = registry.counter("meldinger", "antall", "feilet")

    fun målFeiletFil(lambda: () -> Unit) {
        feiledeFiler.increment()
    }
}