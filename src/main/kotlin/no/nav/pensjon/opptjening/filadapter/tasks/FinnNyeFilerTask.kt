package no.nav.pensjon.opptjening.filadapter.tasks

import no.nav.pensjon.opptjening.filadapter.remote.filsluse.FilsluseClient
import no.nav.pensjon.opptjening.filadapter.repository.FilInfoRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled

class FinnNyeFilerTask(
    private val filInfoRepository: FilInfoRepository,
    private val filsluseClient: FilsluseClient,
) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(FinnNyeFilerTask::class.java)
        private val secureLog = LoggerFactory.getLogger("secureLogger")
    }

    @Scheduled(cron = "*/10 * * * * *")
    fun finnNyeFiler() {
        val filer = filsluseClient.scanForFiles("/")
        filer.forEach {
            secureLog.info("Remote file: $it")
        }
    }
}