package no.nav.tiltakspenger.soknad.api.metrics

import io.prometheus.client.Counter
import io.prometheus.client.Gauge
import io.prometheus.client.Summary

class MetricsCollector {
    private val NAMESPACE = "tpts"

    val ANTALL_SØKNADER_MOTTATT_COUNTER = Counter.build()
        .name("antall_soknader_mottatt")
        .namespace(NAMESPACE)
        .help("Antall søknader mottatt")
        .register()

    val ANTALL_UGYLDIGE_SØKNADER_COUNTER = Counter.build()
        .name("antall_ugyldige_soknader")
        .namespace(NAMESPACE)
        .help("Antall ugyldige søknader forsøkt sendt inn")
        .register()

    val ANTALL_FEILEDE_INNSENDINGER_COUNTER = Counter.build()
        .name("antall_soknader_feilet")
        .namespace(NAMESPACE)
        .help("Antall feilede søknadsinnsendinger")
        .register()

    val ANTALL_SØKNADER_SOM_PROSESSERES = Gauge.build()
        .name("antall_soknader_som_prosesseres")
        .namespace(NAMESPACE)
        .help("Antall søknader som prosesseres akkurat nå")
        .register()

    val SØKNADSMOTTAK_LATENCY_SECONDS = Summary.build()
        .name("soknadsmottak_latency_seconds")
        .namespace(NAMESPACE)
        .help("Hvor lang tid det tar å prosessere en søknad (i sekunder)")
        .register()
}
