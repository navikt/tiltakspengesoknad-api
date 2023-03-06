package no.nav.tiltakspenger.soknad.api.domain

import java.time.LocalDate

data class Periode(
    val fra: LocalDate,
    val til: LocalDate,
)

data class Tiltak(
    val type: String,
    val periode: Periode,
    val antallDagerIUken: Int,
)

data class AnnenUtbetaling(
    val utbetaler: String,
    val periode: Periode,
)

data class Barn(
    val fornavn: String,
    val etternavn: String,
    val fdato: LocalDate,
    val bostedsland: String,
)

data class Søknad(
    val deltarIKvp: Boolean,
    val periodeMedKvp: Periode?,
    val deltarIIntroprogrammet: Boolean,
    val periodeMedIntroprogrammet: Periode?,
    val borPåInstitusjon: Boolean,
    val institusjonstype: String?,
    val tiltak: Tiltak,
    val mottarEllerSøktPensjonsordning: Boolean,
    val pensjon: AnnenUtbetaling?,
    val mottarEllerSøktEtterlønn: Boolean,
    val etterlønn: AnnenUtbetaling?,
    val søkerOmBarnetillegg: Boolean,
    val barnSøktBarnetilleggFor: List<Barn>?,
)
