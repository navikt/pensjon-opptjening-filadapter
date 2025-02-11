package no.nav.pensjon.opptjening.filadapter.repository

import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import java.time.LocalDateTime

class FilInfoRepositoryImpl : FilInfoRepository {

    companion object {
        val log = NAVLog(FilInfoRepositoryImpl::class)
    }
    override fun lagreFilInfo(filinfo: FilInfo) {
    }

    override fun hentFilInfo(id: FilInfo.Id): FilInfo {
        return FilInfo(id, LocalDateTime.now(), FilInfo.Status.NY)
    }

    override fun lockFileWithStatus(status: FilInfo.Status): FilInfo.Id? {
        TODO("Not yet implemented")
    }

}