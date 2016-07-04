import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by juan.saravia on 04/07/2016.
 */
class LoadXml {

    constructor(file: File) {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(file)
        doc.documentElement.normalize()
        System.out.println("Root element :" + doc.documentElement.nodeName)
        val nList = doc.getElementsByTagName("dialog_call")
    }
}