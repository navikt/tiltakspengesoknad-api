package no.nav.tiltakspenger.soknad.api.soknad

import io.ktor.http.content.MultiPartData
import no.nav.tiltakspenger.soknad.api.pdl.PersonDTO
import no.nav.tiltakspenger.soknad.api.vedlegg.Vedlegg
import java.time.LocalDateTime

interface SøknadService {
    suspend fun opprettDokumenterOgArkiverIJoark(søknad: SpørsmålsbesvarelserDTO, fnr: String, person: PersonDTO, vedlegg: List<Vedlegg>, acr: String, innsendingTidspunkt: LocalDateTime): String
    suspend fun taInnSøknadSomMultipart(søknadSomMultipart: MultiPartData): Pair<SpørsmålsbesvarelserDTO, List<Vedlegg>>
}
