import org.junit.Test
import java.io.File

/**
 * Created by juan.saravia on 04/07/2016.
 */
class LanguagesManagerTest() {

    @Test fun testA() {
        val excelFile = File(javaClass.classLoader.getResource("android_translations_sample.xlsx").file)
        val lm = LanguagesManager(excelFile)
    }
}