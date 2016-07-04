import java.io.File

/**
 * Created by juan.saravia on 04/07/2016.
 */
fun String.loadFileFromResources(): File {
    val classLoader = javaClass.classLoader
    val f = classLoader.getResource(this)?.file
    return File(f)
}