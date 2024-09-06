package no.nav.tiltakspenger.soknad.api.db

import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import no.nav.tiltakspenger.soknad.api.Configuration.database

private val LOG = KotlinLogging.logger {}

object DataSource {
    private val config = database()
    private const val MAX_POOLS = 3
    const val FAIL_TIMEOUT = 5000

    private fun init(): HikariDataSource {
        return HikariDataSource().apply {
            dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
            addDataSourceProperty("jdbcUrl", config.url)
            initializationFailTimeout = FAIL_TIMEOUT.toLong()
            maximumPoolSize = MAX_POOLS
        }
    }

    val hikariDataSource: HikariDataSource by lazy {
        init()
    }
}
