package no.nav.tiltakspenger.soknad.api.util

import org.apache.commons.text.StringEscapeUtils

object StringSanitizer {
    fun sanitize(str: String): String {
        val escapedXml11 = StringEscapeUtils.escapeXml11(str)
        val escapedES = StringEscapeUtils.escapeEcmaScript(escapedXml11)
        return escapedES.replace(Regex("""(\\u00E6|\\u00F8|\\u00E5|\\u00C6|\\u00D8|\\u00C5)""")) { match ->
            when (match.value) {
                "\\u00E6" -> "æ"
                "\\u00F8" -> "ø"
                "\\u00E5" -> "å"
                "\\u00C6" -> "Æ"
                "\\u00D8" -> "Ø"
                "\\u00C5" -> "Å"
                else -> ""
            }
        }
    }
}
