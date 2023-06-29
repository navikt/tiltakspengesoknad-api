package no.nav.tiltakspenger.soknad.api.util

import org.apache.commons.text.StringEscapeUtils

object StringSanitizer {
    fun sanitize(str: String): String = StringEscapeUtils.escapeXml11(str)
}
