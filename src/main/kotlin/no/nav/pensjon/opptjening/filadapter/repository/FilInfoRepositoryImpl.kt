package no.nav.pensjon.opptjening.filadapter.repository

import no.nav.pensjon.opptjening.filadapter.log.NAVLog
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.LocalDateTime

class FilInfoRepositoryImpl(
    val jdbcTemplate: NamedParameterJdbcTemplate
) : FilInfoRepository {

    companion object {
        val log = NAVLog(FilInfoRepositoryImpl::class)
    }
    override fun lagreFilInfo(filinfo: FilInfo) {
        jdbcTemplate.update(
            """
            insert into filinfo(filnavn, dato, status) values (:filnavn, :dato, :status)
        """.trimIndent(), MapSqlParameterSource(
                mapOf(
                    "filnavn" to filinfo.id.filnavn,
                    "dato" to filinfo.dato,
                    "status" to filinfo.status.toString(),
                )
            )
        )
    }

    override fun hentFilInfo(id: FilInfo.Id): FilInfo {
        jdbcTemplate.queryForList(
            """
                select filnavn from filinfo
            """.trimIndent(),
            MapSqlParameterSource(
                mapOf("eple" to "banan")
            )
        ).map {
            log.secure.info("hentFilInfo: ${it["filnavn"]}")
        }
        return FilInfo(id, dato = LocalDateTime.now(), status = FilInfo.Status.IKKE_BEHANDLES)
    }

    override fun lockFileWithStatus(status: FilInfo.Status): FilInfo.Id? {
        TODO("Not yet implemented")
    }

}