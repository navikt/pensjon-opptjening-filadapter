package no.nav.pensjon.opptjening.filadapter.web.dto

import java.time.Instant

data class ListFilerResponse(
    val filer: List<FilMedStatus>,
    val side: Int,
    val antall: Int,
    val totalt: Int,
    val harMer: Boolean,
) {
    data class FilMedStatus(
        val filnavn: String,
        val size: Long,
        val lagretMedId: String?,
        val lagresMedId: List<String>,
        val modifiedAt: Instant,
        val prosessert: Boolean,
    )
}
