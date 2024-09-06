package no.nav.tiltakspenger.soknad.api.soknad

import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.tiltakspenger.soknad.api.db.DataSource
import no.nav.tiltakspenger.soknad.api.serialize
import org.intellij.lang.annotations.Language

class SøknadRepoImpl() : SøknadRepo {
    override fun lagre(søknad: Søknad) {
        sessionOf(DataSource.hikariDataSource).use {
            it.transaction { transaction ->
                transaction.run(
                    queryOf(
                        sqlLagre,
                        mapOf(
                            "id" to søknad.id.toString(),
                            "versjon" to søknad.versjon,
                            "soknad" to serialize(søknad.søknadSpm),
                            "vedlegg" to serialize(søknad.vedlegg),
                            "fnr" to søknad.fnr,
                            "sendtTilVedtak" to søknad.sendtTilVedtak,
                            "journalfort" to søknad.journalført,
                            "opprettet" to søknad.opprettet,
                        ),
                    ).asUpdate,
                )
            }
        }
    }

    @Language("PostgreSQL")
    private val sqlLagre =
        """
        insert into soknad (
            id,
            versjon,
            søknad,
            vedlegg,
            fnr,
            sendt_til_vedtak,
            journalfort,
            opprettet
        ) values (
            :id,
            :versjon,
            :soknad,
            :vedlegg,
            :fnr,
            :sendtTilVedtak,
            :journalfort,
            :opprettet
        )
        """.trimIndent()
}
