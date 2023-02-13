package no.nav.tiltakspengesoknad.api.soknad

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import no.nav.tiltakspengesoknad.api.SØKNAD_URL

val LOG = KotlinLogging.logger { }

fun Route.soknadRoutes() {
  route(SØKNAD_URL) {
    get {
      call.respondText(text = "Dette er en test!", contentType = ContentType.Text.Plain, status = HttpStatusCode.OK)
    }
  }.also { LOG.info { "satt opp endepunkt /soknad" } }
}