package no.nav.tiltakspenger.soknad.api.metrics

import io.prometheus.client.Counter
import io.prometheus.client.Summary

class MetricsCollector {
    private val namespace = "tpts"

    val antallSøknaderMottattCounter: Counter = Counter.build()
        .name("tiltakspenger_soknad_antall_soknader_mottatt")
        .namespace(namespace)
        .help("Antall søknader mottatt")
        .register()

    val antallUgyldigeSøknaderCounter: Counter = Counter.build()
        .name("tiltakspenger_soknad_antall_ugyldige_soknader")
        .namespace(namespace)
        .help("Antall ugyldige søknader forsøkt sendt inn")
        .register()

    val antallFeiledeInnsendingerCounter: Counter = Counter.build()
        .name("tiltakspenger_soknad_antall_soknader_feilet")
        .namespace(namespace)
        .help("Antall feilede søknadsinnsendinger")
        .register()

    val antallFeilVedHentPersonaliaCounter: Counter = Counter.build()
        .name("tiltakspenger_soknad_antall_feil_ved_hent_personalia")
        .namespace(namespace)
        .help("Antall ganger personalia-kall har feilet")
        .register()

    val antallFeilVedHentTiltakCounter: Counter = Counter.build()
        .name("tiltakspenger_soknad_antall_feil_ved_hent_tiltak")
        .namespace(namespace)
        .help("Antall ganger tiltak-kall har feilet")
        .register()

    val søknadsmottakLatencySeconds: Summary = Summary.build()
        .name("tiltakspenger_soknad_soknadsmottak_latency_seconds")
        .namespace(namespace)
        .help("Hvor lang tid det tar å prosessere en søknad (i sekunder)")
        .register()
}
