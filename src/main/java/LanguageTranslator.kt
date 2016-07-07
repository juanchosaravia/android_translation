import java.io.Closeable
import java.io.File
import java.nio.file.Files
import java.rmi.UnexpectedException
import java.util.*

/**
 * Created by juan.saravia on 04/07/2016.
 */
class LanguageTranslator(excelFile: File) : Closeable {

    private val excelManager: ExcelManager
    private val basePath: String
    private var symbols: MutableMap<String, String> = HashMap()

    private val configSheet = 0
    private val configBasePathRow = 0
    private val configBasePathCell = 1

    private val translationSheet = 1
    private val translationIsEnableCol = 0
    private val translationRelativePathCol = 1
    private val translationFileNameCol = 2
    private val translationTagNameCol = 3
    private val translationFirstLanguageCol = 4

    private val resourceFolderName = "values"
    private val allowNewFileFromScratch = true
    private val IGNORE = "IGNORE"

    constructor(excelPath: String) : this(File(excelPath))

    init {
        excelManager = ExcelManager(excelFile)
        basePath = excelManager.getStringValue(configSheet, configBasePathRow, configBasePathCell)
        // TODO: Check excel has correct format

        symbols = HashMap<String, String>()
        symbols.put("®", "\\u00AE")
        symbols.put("&\\s", "&amp;")
        symbols.put("©", "\\u00A9")
        symbols.put("'", "\\'")
    }

    fun translate() {

        val langCount = getLanguageCounts() - 1
        val itemsCount = getItemsToTranslateCount() + 2 // for some reason the lib don't return the num of rows properly.

        for (lngCol in translationFirstLanguageCol..(translationFirstLanguageCol + langCount)) {

            val languageId = excelManager.getStringValue(translationSheet, 0, lngCol)

            if (languageId.isEmpty() || languageId.startsWith("ignore-")) {
                continue
            }

            for (itemRow in 1..itemsCount) {

                val itemIsEnable = excelManager.getStringValue(translationSheet, itemRow, translationIsEnableCol)

                if (itemIsEnable.toLowerCase() != "true") {
                    // ignore any other state and process just items with "TRUE" value
                    logln("Row ${itemRow + 1} Disable")
                    continue
                }

                val itemRelativePath = excelManager.getStringValue(translationSheet, itemRow, translationRelativePathCol)
                val itemFileName = excelManager.getStringValue(translationSheet, itemRow, translationFileNameCol)
                val itemTagName = excelManager.getStringValue(translationSheet, itemRow, translationTagNameCol)
                val itemTagValue = excelManager.getStringValue(translationSheet, itemRow, lngCol)

                if (itemTagValue.toUpperCase() == IGNORE) {
                    // allows you to ignore an specific cell translation
                    logln("Row ${itemRow + 1} -> Lng: $languageId - Tag: $itemTagName | $IGNORE")
                    continue
                }

                log("Row ${itemRow + 1} ENABLE -> ")

                val baseDirectory = File(basePath)
                val subDirectory = File(baseDirectory, itemRelativePath)

                val enResDirectory = File(subDirectory, resourceFolderName)
                val englishFile = File(enResDirectory, itemFileName)

                //val enRgbResDirectory = File(subDirectory, "values-en-rGB")
                //val englishRgbFile = File(enRgbResDirectory, itemFileName)

                val otherLngDirectory = File(subDirectory, "$resourceFolderName-$languageId")
                val otherLanguageFile = File(otherLngDirectory, itemFileName)
                var isNewEmptyFile = false

                if (!otherLanguageFile.exists()) {
                    //if (!englishRgbFile.exists() && !englishFile.exists() && !allowNewFileFromScratch) {
                    if (!englishFile.exists() && !allowNewFileFromScratch) {
                        throw UnexpectedException("English file doesn't exists, I'm unable to create a copy.")
                    }
                    // check folder for language exists or create it
                    if (!otherLanguageFile.parentFile.exists() && !otherLanguageFile.parentFile.mkdirs()) {
                        throw UnexpectedException("There was an error trying to create the folder for language: ${otherLanguageFile.absolutePath}")
                    }

                    log("file didn't exists -> ")
                    /*if (englishRgbFile.exists()) {
                        Files.copy(englishRgbFile.toPath(), otherLanguageFile.toPath())
                        log("copied from values-en-rGB -> ")
                    } else*/
                    if (englishFile.exists()) {
                        // copy eng file
                        Files.copy(englishFile.toPath(), otherLanguageFile.toPath())
                        log("copied from english -> ")
                    } else {
                        isNewEmptyFile = true
                    }
                }

                val otherXml = XmlParser(otherLanguageFile, isNewEmptyFile)
                if (isNewEmptyFile) {
                    otherXml.appendElement("resources")
                    log("file didn't exists -> created from scratch -> ")
                }

                otherXml.setOrCreateValueTag(itemTagName, replaceSymbols(itemTagValue))
                otherXml.close()
                logln("Tag: $itemTagName | DONE")
            }

        }
    }

    private fun getItemsToTranslateCount(): Int {
        return excelManager.getRowCountBySheet(translationSheet, 1)
    }

    private fun getLanguageCounts(): Int {
        return excelManager.getColCountBySheet(translationSheet) - translationFirstLanguageCol
    }

    override fun close() {
        excelManager.close()
    }

    private fun replaceSymbols(value: String): String {
        var result = value
        for (symbol in symbols.entries) {
            result = result.replace(symbol.key, symbol.value)
        }
        return result
    }

    private fun log(msg: String) {
        System.out.print(msg)
    }

    private fun logln(msg: String) {
        System.out.println(msg)
    }
}