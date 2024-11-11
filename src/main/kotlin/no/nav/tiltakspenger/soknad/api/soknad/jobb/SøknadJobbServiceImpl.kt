package no.nav.tiltakspenger.soknad.api.soknad.jobb

import no.nav.tiltakspenger.libs.common.CorrelationId
import no.nav.tiltakspenger.libs.common.Fnr
import no.nav.tiltakspenger.libs.logging.sikkerlogg
import no.nav.tiltakspenger.soknad.api.saksbehandlingApi.SendSøknadTilSaksbehandlingApiService
import no.nav.tiltakspenger.soknad.api.soknad.SøknadRepo
import no.nav.tiltakspenger.soknad.api.soknad.SøknadService
import no.nav.tiltakspenger.soknad.api.soknad.jobb.person.PersonGateway
import no.nav.tiltakspenger.soknad.api.soknad.log
import java.time.LocalDateTime

class SøknadJobbServiceImpl(
    private val søknadRepo: SøknadRepo,
    private val personGateway: PersonGateway,
    private val søknadService: SøknadService,
    private val sendSøknadTilSaksbehandlingApiService: SendSøknadTilSaksbehandlingApiService,
) : SøknadJobbService {
    override suspend fun journalførLagredeSøknader(correlationId: CorrelationId) {
        søknadRepo.hentAlleSøknadDbDtoSomIkkeErJournalført().forEach { søknad ->
            log.info { "Journalfør søknad jobb: Prøver å journalføre søknad med søknadId ${søknad.id}" }

            val navn = try {
                personGateway.hentNavnForFnr(Fnr.fromString(søknad.fnr))
            } catch (e: Exception) {
                log.error(RuntimeException("Trigger stacktrace for enklere debug.")) { "Journalfør søknad jobb: Feil ved henting av navn fra PDL for søknadId ${søknad.id}" }
                sikkerlogg.error(e) { "Journalfør søknad jobb: Feil ved henting av navn fra PDL for søknadId ${søknad.id}" }
                return@forEach
            }
            val (journalpostId, søknadDto) = try {
                søknadService.opprettDokumenterOgArkiverIJoark(
                    spørsmålsbesvarelser = søknad.søknadSpm,
                    fnr = søknad.fnr,
                    fornavn = navn.fornavn,
                    etternavn = navn.etternavn,
                    vedlegg = søknad.vedlegg,
                    acr = søknad.acr,
                    innsendingTidspunkt = søknad.opprettet,
                    søknadId = søknad.id,
                    callId = correlationId.toString(),
                )
            } catch (e: Exception) {
                log.error(RuntimeException("Trigger stacktrace for enklere debug.")) { "Journalfør søknad jobb: Feil under journalføring mot Joark for søknadId ${søknad.id}" }
                sikkerlogg.error(e) { "Journalfør søknad jobb: Feil under journalføring mot Joark for søknadId ${søknad.id}" }
                return@forEach
            }
            søknadRepo.oppdater(
                søknad.copy(
                    søknad = søknadDto,
                    fornavn = navn.fornavn,
                    etternavn = navn.etternavn,
                    journalpostId = journalpostId,
                    journalført = LocalDateTime.now(),
                ),
            )
            log.info { "Journalfør søknad jobb: Vi har journalført søknad ${søknad.id} " }
        }
    }

    override suspend fun sendJournalførteSøknaderTilSaksbehandlingApi(correlationId: CorrelationId) {
        søknadRepo.hentSøknaderSomSkalSendesTilSaksbehandlingApi().forEach { søknad ->
            checkNotNull(søknad.søknad) { "Send søknad til saksbehandling-api jobb: Søknad ${søknad.id} mangler søknad" }
            checkNotNull(søknad.journalpostId) { "Send søknad til saksbehandling-api jobb: Søknad ${søknad.id} mangler journalpostId" }
            try {
                val sendtTilSaksbehandlingApi = LocalDateTime.now()
                sendSøknadTilSaksbehandlingApiService.sendSøknad(søknad.søknad, søknad.journalpostId, correlationId)
                log.info { "Send søknad til saksbehandling-api jobb: Søknad ${søknad.id} er sendt til saksbehandling-api - prøver lagre utsendingstidspuktet" }
                søknadRepo.oppdater(søknad.copy(sendtTilVedtak = sendtTilSaksbehandlingApi))
                log.info { "Send søknad til saksbehandling-api jobb: Oppdatert utsendingstidspunktet til $sendtTilSaksbehandlingApi for søknad ${søknad.id}" }
            } catch (e: Exception) {
                log.error(RuntimeException("Trigger stacktrace for enklere debug.")) { "Send søknad til saksbehandling-api jobb: Feil ved sending av søknad ${søknad.id} til saksbehandling-api. Denne vil prøves på nytt. Se sikkerlogg for mer info." }
                sikkerlogg.error(e) { "Send søknad til saksbehandling-api jobb:  Feil ved sending av søknad ${søknad.id} til saksbehandling-api. Denne vil prøves på nytt." }
            }
        }
    }
}
