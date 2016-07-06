import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Created by juan.saravia on 04/07/2016.
 */
class XmlParser : Closeable {

    val xPathfactory: XPathFactory
    var doc: Document
    val file: File

    constructor(file: File, isNewEmptyFile: Boolean = true) {
        this.file = file
        xPathfactory = XPathFactory.newInstance()
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        if (isNewEmptyFile) {
            doc = builder.newDocument()
        } else {
            doc = builder.parse(file)
        }
        doc.xmlStandalone = true
    }

    fun getTagValue(tagName: String): String? {
        return getTag(tagName)?.nodeValue
    }

    private fun getTag(tagName: String): Node? {
        val xpath = xPathfactory.newXPath()
        val expr = xpath.compile("//string[@name=\"$tagName\"]")
        return expr.evaluate(doc, XPathConstants.NODE) as? Node
    }

    fun setOrCreateValueTag(attrValue: String, tagValue: String, tagName: String = "string", attrName: String = "name") {
        val node = getTag(attrValue)
        if (node != null) {
            node.firstChild?.nodeValue = tagValue
        } else {
            val element = doc.createElement(tagName)
            element.setAttribute(attrName, attrValue)
            element.appendChild(doc.createTextNode(tagValue))
            doc.firstChild.appendChild(element)
        }
    }

    fun appendElement(tagName: String) {
        val rootElement = doc.createElement(tagName)
        doc.appendChild(rootElement)
    }

    override fun close() {
        val transformerFactory = TransformerFactory.newInstance()
        transformerFactory.setAttribute("indent-number", 4);
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
        //transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        //transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
        //transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");

        val outputXmlStringWriter = StringWriter()
        transformer.transform(DOMSource(doc), StreamResult(outputXmlStringWriter));
        val outputXmlString = outputXmlStringWriter.toString().replaceFirst("?><", "?>\n<")

        val outputXml = FileOutputStream(file)
        outputXml.write(outputXmlString.toByteArray(charset("UTF-8")))
    }
}