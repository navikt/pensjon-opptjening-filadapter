package no.nav.pensjon.opptjening.filadapter

import org.slf4j.MDC

object Mdc {
    fun <T> scopedMdc(ettellerannet: String, block: (ettellerannet: String) -> T): T {
        return MDC.putCloseable("ettellerannet", ettellerannet).use { block(ettellerannet) }
    }
}