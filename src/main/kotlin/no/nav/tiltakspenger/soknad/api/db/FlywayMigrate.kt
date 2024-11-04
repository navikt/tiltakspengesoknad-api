package no.nav.tiltakspenger.soknad.api.db

import no.nav.tiltakspenger.soknad.api.Configuration
import no.nav.tiltakspenger.soknad.api.Profile
import org.flywaydb.core.Flyway

private fun flyway(): Flyway =
    when (Configuration.applicationProfile()) {
        Profile.LOCAL -> localFlyway()
        else -> gcpFlyway()
    }

internal fun localFlyway() = Flyway
    .configure()
    .loggers("slf4j")
    .encoding("UTF-8")
    .locations("db/migration", "db/local-migration")
    .dataSource(DataSource.hikariDataSource)
    .cleanDisabled(false)
    .cleanOnValidationError(true)
    .load()

private fun gcpFlyway() = Flyway
    .configure()
    .loggers("slf4j")
    .encoding("UTF-8")
    .dataSource(DataSource.hikariDataSource)
    .cleanDisabled(true)
    .cleanOnValidationError(false)
    .load()

fun flywayMigrate() {
    flyway().migrate()
}
