package no.nav.pensjon.opptjening.filadapter.web.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import org.springframework.stereotype.Component

@Component
class LoggingFilter : Filter {
    private val log = NAVLog(LoggingFilter::class)

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest) {
            val httpResponse = response as HttpServletResponse
            chain.doFilter(request, response)
            log.open.info("${request.requestURL} -> ${httpResponse.status}")
        }
    }
}