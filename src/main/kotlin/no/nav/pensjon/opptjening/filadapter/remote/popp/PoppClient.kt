package no.nav.pensjon.opptjening.filadapter.remote.popp

import java.io.InputStream
import java.util.*

interface PoppClient {
    fun lagreFil(fil: InputStream) : UUID
    fun validerFil(id: UUID): Boolean
}