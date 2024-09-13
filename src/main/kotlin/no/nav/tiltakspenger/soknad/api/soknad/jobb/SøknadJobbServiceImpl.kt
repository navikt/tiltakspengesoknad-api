package no.nav.tiltakspenger.soknad.api.soknad.jobb

import no.nav.tiltakspenger.libs.common.CorrelationId
import no.nav.tiltakspenger.libs.common.Fnr
import no.nav.tiltakspenger.soknad.api.Configuration.applicationProfile
import no.nav.tiltakspenger.soknad.api.Profile
import no.nav.tiltakspenger.soknad.api.soknad.SøknadRepo
import no.nav.tiltakspenger.soknad.api.soknad.SøknadService
import no.nav.tiltakspenger.soknad.api.soknad.jobb.person.PersonGateway
import no.nav.tiltakspenger.soknad.api.soknad.log
import no.nav.tiltakspenger.soknad.api.vedtak.VedtakService
import java.time.LocalDateTime

class SøknadJobbServiceImpl(
    private val søknadRepo: SøknadRepo,
    private val personGateway: PersonGateway,
    private val søknadService: SøknadService,
    private val vedtakService: VedtakService,
) : SøknadJobbService {
    override suspend fun journalførLagredeSøknader(correlationId: CorrelationId) {
        søknadRepo.hentAlleSøknadDbDtoSomIkkeErJournalført()
            .forEach { søknad ->
                log.info { "Vi skal prøve å journalføre søknad : ${søknad.id}" }

                val navn = try {
                    personGateway.hentNavnForFnr(Fnr.fromString(søknad.fnr))
                } catch (e: Exception) {
                    log.error { "Feil ved henting av personnavn for søknad: ${søknad.id}" }
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
                        innsendingTidspunkt = LocalDateTime.now(),
                        søknadId = søknad.id,
                        callId = correlationId.toString(),
                    )
                } catch (e: Exception) {
                    log.error { "Feil under journalføring av søknad : ${søknad.id}" }
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

                log.info { "Vi har journalført søknad ${søknad.id} " }
            }
    }

    override suspend fun sendJournalgørteSøknaderTilVedtak(correlationId: CorrelationId) {
        if (applicationProfile() == Profile.DEV) {
            søknadRepo.hentAlleSøknadDbDtoSomErJournalførtMenIkkeSendtTilVedtak()
                .forEach { søknad ->
                    checkNotNull(søknad.søknad) { "Kan ikke sende til vedtak da det mangler søknad" }
                    checkNotNull(søknad.journalpostId) { "Kan ikke sende til vedtak da det mangler journalpostId" }
                    try {
                        vedtakService.sendSøknad(søknad.søknad, søknad.journalpostId, correlationId)
                        log.info { "Vi har sendt søknad ${søknad.id} til vedtak" }
                        søknadRepo.oppdater(søknad.copy(sendtTilVedtak = LocalDateTime.now()))
                    } catch (e: Exception) {
                        log.error { "Feil ved sending av søknad til vedtak: ${søknad.id}" }
                    }
                }
        }
    }
}
