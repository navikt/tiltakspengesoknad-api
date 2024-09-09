package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.http.content.MultiPartData
import no.nav.tiltakspenger.libs.common.CorrelationId
import no.nav.tiltakspenger.soknad.api.pdl.PersonDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.time.LocalDateTime

interface SøknadService {
    suspend fun journalførLagredeSøknader(correlationId: CorrelationId)
    suspend fun opprettDokumenterOgArkiverIJoark(spørsmålsbesvarelser: SpørsmålsbesvarelserDTO, fnr: String, person: PersonDTO, vedlegg: List<Vedlegg>, acr: String, innsendingTidspunkt: LocalDateTime, callId: String): String
    suspend fun taInnSøknadSomMultipart(søknadSomMultipart: MultiPartData): Pair<SpørsmålsbesvarelserDTO, List<Vedlegg>>
}
