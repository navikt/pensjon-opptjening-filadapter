package no.nav.pensjon.opptjening.filadapter.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.DatabaseStartupValidator
import javax.sql.DataSource

@Configuration
class DatabaseStartupValidatorConfig {
    @Bean
    fun databaseStartupValidator(dataSource: DataSource) = DatabaseStartupValidator().apply {
        println("databaseStartupValidator")
        setDataSource(dataSource)
    }

    @Bean
    fun dependsOnPostProcessor(): BeanFactoryPostProcessor? {
        println("dependsOnPostProcessor")
        return BeanFactoryPostProcessor { bf: ConfigurableListableBeanFactory ->
            val flyway = bf.getBeanNamesForType(Flyway::class.java)
            println("flyway=${flyway.toList()}")
            flyway.map { beanName: String? -> bf.getBeanDefinition(beanName!!) }
                .forEach { it.setDependsOn("databaseStartupValidator") }
        }
    }
}