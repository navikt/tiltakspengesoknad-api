package no.nav.tiltakspenger.soknad.api.soknad

import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.tiltakspenger.soknad.api.db.DataSource
import no.nav.tiltakspenger.soknad.api.serialize
import org.intellij.lang.annotations.Language

class SøknadRepoImpl() : SøknadRepo {
    override fun lagre(dto: SøknadDbDTO) {
        sessionOf(DataSource.hikariDataSource).use {
            it.transaction { transaction ->
                transaction.run(
                    queryOf(
                        sqlLagre,
                        mapOf(
                            "id" to dto.id.toString(),
                            "versjon" to dto.versjon,
                            "soknad" to serialize(dto.søknadSpm),
                            "vedlegg" to serialize(dto.vedlegg),
                            "fnr" to dto.fnr,
                            "sendtTilVedtak" to dto.sendtTilVedtak,
                            "journalfort" to dto.journalført,
                            "opprettet" to dto.opprettet,
                        ),
                    ).asUpdate,
                )
            }
        }
    }

    @Language("PostgreSQL")
    private val sqlLagre =
        """
        insert into søknad (
            id,
            versjon,
            søknad,
            vedlegg,
            fnr,
            sendt_til_vedtak,
            journalført,
            opprettet
        ) values (
            :id,
            :versjon,
            to_jsonb(:soknad::jsonb),
            to_jsonb(:vedlegg::jsonb),
            :fnr,
            :sendtTilVedtak,
            :journalfort,
            :opprettet
        )
        """.trimIndent()
}
