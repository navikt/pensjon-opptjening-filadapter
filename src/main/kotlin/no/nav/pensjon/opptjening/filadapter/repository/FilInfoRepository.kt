package no.nav.pensjon.opptjening.filadapter.repository

interface FilInfoRepository {
    fun lagreFilInfo(filinfo: FilInfo)
    fun hentFilInfo(filinfo: FilInfo.Id): FilInfo
    fun lockFileWithStatus(status: FilInfo.Status): FilInfo.Id?
}