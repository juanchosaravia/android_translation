import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by juan.saravia on 04/07/2016.
 */
public class LoadExcelTest {

    @Test
    public void testOpenFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("android_translations_sample.xlsx").getFile());
        try(LoadExcel excel = new LoadExcel(file)) {
            String value = excel.getTitleName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
