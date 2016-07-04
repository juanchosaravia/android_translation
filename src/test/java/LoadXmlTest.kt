import org.junit.Test

/**
 * Created by juan.saravia on 04/07/2016.
 */

class LoadXmlTest() {

    @Test fun testA() {
        val xml = LoadXml("strings.xml".loadFileFromResources())
    }
}