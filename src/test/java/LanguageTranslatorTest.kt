import org.junit.Test
import java.io.File

/**
 * Created by juan.saravia on 04/07/2016.
 */
class LanguageTranslatorTest() {

    @Test fun testA() {
        val excelFile = File(javaClass.classLoader.getResource("android_translations_sample_v1.xlsx").file)
        val lm = LanguageTranslator(excelFile)
        lm.translate()
    }

    @Test fun testTranslateAll() {
        val excelFile = File(javaClass.classLoader.getResource("android_translations_sample_v2.xlsx").file)
        val lm = LanguageTranslator(excelFile)
        lm.translate()
    }
}