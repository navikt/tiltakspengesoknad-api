package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.http.content.MultiPartData
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.time.LocalDateTime

interface SøknadService {
    suspend fun opprettDokumenterOgArkiverIJoark(spørsmålsbesvarelser: SpørsmålsbesvarelserDTO, fnr: String, fornavn: String, etternavn: String, vedlegg: List<Vedlegg>, acr: String, innsendingTidspunkt: LocalDateTime, callId: String): String
    suspend fun taInnSøknadSomMultipart(søknadSomMultipart: MultiPartData): Pair<SpørsmålsbesvarelserDTO, List<Vedlegg>>
}
