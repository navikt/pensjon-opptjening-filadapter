package no.nav.pensjon.opptjening.filadapter.config

import com.zaxxer.hikari.HikariDataSource

class DatasourceReadinessCheck(
    private val datasource: HikariDataSource
) {
    fun isReady(): Boolean {
        return !datasource.isClosed
    }
}