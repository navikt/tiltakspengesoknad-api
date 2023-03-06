package no.nav.tiltakspenger.soknad.api.health

interface HealthCheck {
    val name: String

        get() = this.javaClass.simpleName

    fun status(): HealthStatus
}

enum class HealthStatus { HEALTHY, UNHEALTHY }
