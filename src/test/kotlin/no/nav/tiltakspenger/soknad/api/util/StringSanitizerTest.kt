package no.nav.tiltakspenger.soknad.api.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class StringSanitizerTest {
    @Test
    fun `sanitize skal returnere string med potensiell XSS escapet`() {
        val testString = "<script>Hello</script>"
        val escapedTestString = StringSanitizer.sanitize(testString)
        assertEquals("""&lt;script&gt;Hello&lt;\/script&gt;""", escapedTestString)
    }

    @Test
    fun `sanitize skal ikke escape æ, ø eller å`() {
        val testString = "<script>æ ø å Æ Ø Å</script>"
        val escapedTestString = StringSanitizer.sanitize(testString)
        assertEquals("""&lt;script&gt;æ ø å Æ Ø Å&lt;\/script&gt;""", escapedTestString)
    }
}
