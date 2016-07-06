import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Created by juan.saravia on 04/07/2016.
 */

class XmlParserTest() {

    @Test fun testGetTagValue() {
        val file = File(javaClass.classLoader.getResource("strings.xml").file)
        val xml = XmlParser(file)
        val value = xml.getTagValue("dialog_call")
        assertEquals("Call", value, "Value is different than expected.")
    }

    @Test fun testGetUnexistingTag() {
        val file = File(javaClass.classLoader.getResource("strings.xml").file)
        val xml = XmlParser(file)
        val value = xml.getTagValue("invalid_tag")
        assertNull(value, "value should be null")
    }
}