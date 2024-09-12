package no.nav.tiltakspenger.soknad.api.metrics

import io.prometheus.client.Counter
import io.prometheus.client.Summary

class MetricsCollector {
    private val NAMESPACE = "tpts"

    val ANTALL_SØKNADER_MOTTATT_COUNTER = Counter.build()
        .name("tiltakspenger_soknad_antall_soknader_mottatt")
        .namespace(NAMESPACE)
        .help("Antall søknader mottatt")
        .register()

    val ANTALL_UGYLDIGE_SØKNADER_COUNTER = Counter.build()
        .name("tiltakspenger_soknad_antall_ugyldige_soknader")
        .namespace(NAMESPACE)
        .help("Antall ugyldige søknader forsøkt sendt inn")
        .register()

    val ANTALL_FEILEDE_INNSENDINGER_COUNTER = Counter.build()
        .name("tiltakspenger_soknad_antall_soknader_feilet")
        .namespace(NAMESPACE)
        .help("Antall feilede søknadsinnsendinger")
        .register()

    val ANTALL_FEIL_VED_HENT_PERSONALIA = Counter.build()
        .name("tiltakspenger_soknad_antall_feil_ved_hent_personalia")
        .namespace(NAMESPACE)
        .help("Antall ganger personalia-kall har feilet")
        .register()

    val ANTALL_FEIL_VED_HENT_TILTAK = Counter.build()
        .name("tiltakspenger_soknad_antall_feil_ved_hent_tiltak")
        .namespace(NAMESPACE)
        .help("Antall ganger tiltak-kall har feilet")
        .register()

    val SØKNADSMOTTAK_LATENCY_SECONDS = Summary.build()
        .name("tiltakspenger_soknad_soknadsmottak_latency_seconds")
        .namespace(NAMESPACE)
        .help("Hvor lang tid det tar å prosessere en søknad (i sekunder)")
        .register()
}
