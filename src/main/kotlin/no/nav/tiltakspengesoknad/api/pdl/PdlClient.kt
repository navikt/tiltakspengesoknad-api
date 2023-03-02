package no.nav.tiltakspengesoknad.api.pdl

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.nav.tiltakspengesoknad.api.httpClientCIO

const val INDIVIDSTONAD = "IND"

data class Navn(
    val fornavn: String,
)

data class PdlPerson(
    val navn: List<Navn>,
)

data class PdlResponseData(
    val hentPerson: PdlPerson?,
)

data class Person(val fornavn: String?)

data class HentPersonResponse(
    val data: PdlResponseData? = null,
    val errors: List<PdlError> = emptyList(),
) {
    private fun extractPerson(): PdlPerson? {
        return this.data?.hentPerson
    }

    fun toPerson(): Person {
        val person = extractPerson()
        return Person(
            fornavn = person?.navn?.toString(),
        )
    }
}

class PdlClient(
    private val endpoint: String,
    private val token: String,
    private val httpClient: HttpClient = httpClientCIO(),
) {
    suspend fun fetchPerson(ident: String): Result<HentPersonResponse> {
        return kotlin.runCatching {
            httpClient.post(endpoint) {
                accept(ContentType.Application.Json)
                header("Tema", INDIVIDSTONAD)
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(hentPersonQuery(ident))
            }.body()
        }
    }
}
