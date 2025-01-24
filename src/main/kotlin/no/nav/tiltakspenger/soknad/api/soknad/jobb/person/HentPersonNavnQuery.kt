package no.nav.tiltakspenger.soknad.api.soknad.jobb.person

import no.nav.tiltakspenger.libs.common.Fnr
import no.nav.tiltakspenger.libs.personklient.pdl.GraphqlQuery

internal fun hentPersonNavnQuery(fnr: Fnr): GraphqlQuery {
    return GraphqlQuery(
        query = query,
        variables = mapOf(
            "ident" to fnr.verdi,
        ),
    )
}

private val query = """
query(${'$'}ident: ID!){
    hentPerson(ident: ${'$'}ident) {
        navn(historikk: false) {
            fornavn
            mellomnavn
            etternavn
            folkeregistermetadata {
                ...folkeregistermetadataDetails
            }
            metadata {
                ...metadataDetails
            }
        }
    }
}

fragment folkeregistermetadataDetails on Folkeregistermetadata {
    aarsak
    ajourholdstidspunkt
    gyldighetstidspunkt
    kilde
    opphoerstidspunkt
    sekvens
}

fragment metadataDetails on Metadata {
    endringer {
        kilde
        registrert
        registrertAv
        systemkilde
        type
    }
    master
}
""".trimIndent()
