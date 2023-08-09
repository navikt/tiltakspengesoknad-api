package no.nav.tiltakspenger.soknad.api.pdl

val hentPersonQueryString = PdlClientTokenX::class.java.getResource("/hentPersonQuery.graphql").readText()
val hentBarnQueryString = PdlCredentialsClient::class.java.getResource("/hentBarnQuery.graphql").readText()
val hentAdressebeskyttelseQueryString = PdlClientTokenX::class.java.getResource("/hentAdressebeskyttelseQuery.graphql").readText()

fun hentPersonQuery(ident: String): GraphqlQuery {
    return GraphqlQuery(
        query = hentPersonQueryString,
        variables = mapOf(
            "ident" to ident,
        ),
    )
}

fun hentBarnQuery(ident: String): GraphqlQuery {
    return GraphqlQuery(
        query = hentBarnQueryString,
        variables = mapOf(
            "ident" to ident,
        ),
    )
}

fun hentAdressebeskyttelseQuery(ident: String): GraphqlQuery {
    return GraphqlQuery(
        query = hentAdressebeskyttelseQueryString,
        variables = mapOf(
            "ident" to ident,
        ),
    )
}

data class GraphqlQuery(
    val query: String,
    val variables: Map<String, String>,
)
