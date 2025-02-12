package no.nav.pensjon.opptjening.filadapter.tasks

import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseKlient
import org.springframework.scheduling.annotation.Scheduled

class FinnNyeFilerTask(
    private val filsluseKlient: FilsluseKlient,
) {
    companion object {
        val log = NAVLog(FinnNyeFilerTask::class)
    }

    @Scheduled(cron = "0 */15 * * * *")
    fun finnNyeFiler() {
        try {
            val filer = filsluseKlient.scanForFiles("/")
            log.open.info("Scannet og fant ${filer.size} filer")
            log.open.info("Scannet og fant ${filer.size} filer")
            filer.forEach {
                log.secure.info("Remote file: $it")
            }
        } catch (t: Throwable) {
            log.open.info("Scanning av filer feilet")
            log.secure.info("Scanning av filer feilet", t)
        }
    }
}