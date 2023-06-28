package no.nav.tiltakspenger.soknad.api.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class StringSanitizerTest {
    @Test
    fun `sanitize skal returnere string med potensiell XSS escapet`() {
        val testString = "<script>Hello</script>"
        val escapedTestString = StringSanitizer.sanitize(testString)
        assertEquals(escapedTestString, "&lt;script&gt;Hello&lt;/script&gt;")
    }

    @Test
    fun `sanitize skal ikke escape æ, ø eller å`() {
        val testString = "<script>æ ø å</script>"
        val escapedTestString = StringSanitizer.sanitize(testString)
        assertEquals(escapedTestString, "&lt;script&gt;æ ø å&lt;/script&gt;")
    }
}
