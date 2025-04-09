package no.nav.pensjon.opptjening.filadapter.web.dto

data class ListFilerResponse(
    val filer: List<FilMedStatus>
) {
    data class FilMedStatus(
        val filnavn: String,
        val size: Long,
        val lagret: Boolean,
    )
}
