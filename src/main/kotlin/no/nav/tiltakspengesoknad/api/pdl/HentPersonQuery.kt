package no.nav.tiltakspengesoknad.api.pdl

val query = PdlClient::class.java.getResource("/hentPersonQuery.graphql").readText()

fun hentPersonQuery(ident: String): GraphqlQuery {
    return GraphqlQuery(
        query = query,
        variables = mapOf(
            "ident" to ident,
        ),
    )
}

data class GraphqlQuery(
    val query: String,
    val variables: Map<String, String>,
)
