package no.nav.pensjon.opptjening.filadapter.common

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import javax.sql.DataSource


class PostgresqlTestContainer private constructor(image: String) : PostgreSQLContainer<PostgresqlTestContainer>(image) {

    init {
        start()
    }

    override fun start() {
        super.start()
        super.waitingFor(Wait.defaultWaitStrategy())
    }

    override fun stop() {
        //Do nothing, JVM handles shut down
    }

    fun removeDataFromDB() {
        println("Removing all data from DB")
        removeDataFromDB(dataSource)
    }


    companion object {
//        val instance: PostgresqlTestContainer = PostgresqlTestContainer("postgres:16.4-alpine")
        val instance: PostgresqlTestContainer = PostgresqlTestContainer("postgres:17.2-bookworm")
        private val dataSource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = "jdbc:tc:postgresql:17:///test"
            username = instance.username
            password = instance.password
        })
        fun createInstance(name: String): DataSource {
//            val instance = PostgresqlTestContainer("postgres:16.4-alpine")
            val instance = PostgresqlTestContainer("postgres:17.2-bookworm")
            val dataSource = HikariDataSource(HikariConfig().apply {
                jdbcUrl = "jdbc:tc:postgresql:17:///$name"
                username = instance.username
                password = instance.password
            })
            return dataSource
        }

        fun removeDataFromDB(dataSource: DataSource) {
            dataSource.connection.apply {
                /*
                createStatement().execute(
                    """           
                        -- DELETE FROM FINNES_IKKE;
                    """
                )
                */
                close()
            }
        }
    }
}