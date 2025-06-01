package no.nav.pensjon.opptjening.filadapter.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class NAVLog(kclass: KClass<*>) {
    val open : Logger = LoggerFactory.getLogger(kclass.java)
    val secure : Logger = LoggerFactory.getLogger("secure")
    val team: Logger = LoggerFactory.getLogger("team-logs")
}