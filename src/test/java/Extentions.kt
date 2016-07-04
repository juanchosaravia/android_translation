import java.io.File

/**
 * Created by juan.saravia on 04/07/2016.
 */
fun <T> Class<T>.getFile(filePath: String): File {
    return File(javaClass.classLoader.getResource(filePath).file)
}