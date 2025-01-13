package no.nav.pensjon.opptjening.filadapter

import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val log = NAVLog(Application::class)
    log.open.info("Starting application")
    runApplication<Application>(*args)
}