package no.nav.tiltakspenger.soknad.api.pdl

val hentPersonQuery = PdlClient::class.java.getResource("/hentPersonQuery.graphql").readText()
val hentBarnQuery = PdlClient::class.java.getResource("/hentBarnQuery.graphql").readText()

fun hentPersonQuery(ident: String): GraphqlQuery {
    return GraphqlQuery(
        query = hentPersonQuery,
        variables = mapOf(
            "ident" to ident,
        ),
    )
}

fun hentBarnQuery(ident: String): GraphqlQuery {
    return GraphqlQuery(
        query = hentBarnQuery,
        variables = mapOf(
            "ident" to ident,
        ),
    )
}

data class GraphqlQuery(
    val query: String,
    val variables: Map<String, String>,
)
