package no.nav.pensjon.opptjening.filadapter.config

import no.nav.pensjon.opptjening.filadapter.repository.FilInfoRepository
import no.nav.pensjon.opptjening.filadapter.repository.FilInfoRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RepositoryConfig {

    @Bean
    fun filInfoRepository(
    ): FilInfoRepository {
        return FilInfoRepositoryImpl()
    }
}