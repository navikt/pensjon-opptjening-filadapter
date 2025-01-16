package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.repository.FilInfoRepository
import no.nav.pensjon.opptjening.filadapter.repository.FilInfoRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class RepositoryConfig {

    @Bean
    fun filInfoRepository(
        jdbcTemplate: JdbcTemplate,
    ): FilInfoRepository {
        return FilInfoRepositoryImpl(NamedParameterJdbcTemplate(jdbcTemplate))
    }
}