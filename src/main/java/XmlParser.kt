import org.w3c.dom.Document
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Created by juan.saravia on 04/07/2016.
 */
class XmlParser {

    val xPathfactory: XPathFactory
    val doc: Document

    constructor(file: File) {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        doc = builder.parse(file)
        xPathfactory = XPathFactory.newInstance()
    }

    fun getTagValue(tagName: String): String {
        val xpath = xPathfactory.newXPath()
        val expr = xpath.compile("//string[@name=\"$tagName\"]")
        return expr.evaluate(doc, XPathConstants.STRING).toString()
    }
}