import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by juan.saravia on 04/07/2016.
 */
public class ExcelManagerTest {

    @Test
    public void testOpenFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("android_translations_sample_v1.xlsx").getFile());
        try(ExcelManager excel = new ExcelManager(file)) {
            //String value = excel.getTitleName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
