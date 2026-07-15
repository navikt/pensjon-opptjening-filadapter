package no.nav.pensjon.opptjening.filadapter.remote.filsluse

import java.time.Instant

data class RemoteFilInfo(
    val name: String,
    val size: Long,
    val modifiedAt: Instant,
)