package no.nav.tiltakspenger.soknad.api.pdl

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.nav.tiltakspenger.soknad.api.httpClientCIO

const val INDIVIDSTONAD = "IND"

class PdlClient(
    private val endpoint: String,
    private val token: String,
    private val httpClient: HttpClient = httpClientCIO(),
) {
    suspend fun fetchSøker(ident: String): Result<SøkerRespons> {
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

    suspend fun fetchBarn(ident: String): Result<SøkersBarnRespons> {
        return kotlin.runCatching {
            httpClient.post(endpoint) {
                accept(ContentType.Application.Json)
                header("Tema", INDIVIDSTONAD)
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(hentBarnQuery(ident))
            }.body()
        }
    }
}
