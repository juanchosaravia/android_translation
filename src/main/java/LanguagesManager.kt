import com.sun.javaws.exceptions.InvalidArgumentException
import java.io.Closeable
import java.io.File
import java.nio.file.Files
import java.rmi.UnexpectedException

/**
 * Created by juan.saravia on 04/07/2016.
 */
class LanguagesManager(excelFile: File) : Closeable {

    private val excelManager: ExcelManager
    private val basePath: String
    private var languages: MutableMap<String, String> = mutableMapOf()

    private val configSheet = 0
    private val configBasePathRow = 0
    private val configBasePathCell = 1
    private val languageId = "LanguageID"

    private val translationSheet = 1
    private val translationRelativePathPos = 0
    private val translationFileNamePos = 1
    private val translationTagNamePos = 2
    private val translationFirstLanguage = 3

    private val resourceFolderName = "values"
    private val allowNewFileFromScratch = true

    constructor(excelPath: String) : this(File(excelPath))

    init {
        excelManager = ExcelManager(excelFile)
        basePath = excelManager.getStringValue(configSheet, configBasePathRow, configBasePathCell)
        // TODO: Check excel has correct format
    }

    private fun initLanguages() {
        val languageRowPos = excelManager.getRowPosition(configSheet, languageId)
        if (languageRowPos == 0) {
            throw InvalidArgumentException(arrayOf("$languageId was not found in the Excel at first sheet."))
        }

        var endReached = false
        var index = languageRowPos
        do {
            index++
            val language = excelManager.getStringValue(configSheet, index, 0)
            val folderName = excelManager.getStringValue(configSheet, index, 1)
            if (!language.isEmpty() && !folderName.isEmpty()) {
                languages.put(language, folderName)
            } else {
                endReached = true
            }
        } while (!endReached)

        if (languages.size == 0) {
            throw InvalidArgumentException(arrayOf("You need to define at least one language to translate."))
        }
    }

    fun translate() {

        val langCount = getLanguageCounts() - 1
        val itemsCount = getItemsToTranslateCount() - 1

        for (lngCol in translationFirstLanguage..(translationFirstLanguage + langCount)) {

            val languageId = excelManager.getStringValue(translationSheet, 0, lngCol)

            if (languageId.isEmpty() || languageId.startsWith("ignore-")) {
                continue
            }

            for (itemRow in 1..itemsCount) {

                val itemRelativePath = excelManager.getStringValue(translationSheet, itemRow, translationRelativePathPos)
                val itemFileName = excelManager.getStringValue(translationSheet, itemRow, translationFileNamePos)
                val itemTagName = excelManager.getStringValue(translationSheet, itemRow, translationTagNamePos)
                val itemTagValue = excelManager.getStringValue(translationSheet, itemRow, lngCol)

                val baseDirectory = File(basePath)
                val subDirectory = File(baseDirectory, itemRelativePath)

                if (!subDirectory.exists()) {
                    /**
                     * Ignore rows without path.
                     * This will allow us to put empty rows or comments
                     */
                    continue
                }

                val enResDirectory = File(subDirectory, resourceFolderName)
                val englishFile = File(enResDirectory, itemFileName)

                val otherLngDirectory = File(subDirectory, "$resourceFolderName-$languageId")
                val otherLanguageFile = File(otherLngDirectory, itemFileName)
                var isNewEmptyFile = false

                if (!otherLanguageFile.exists()) {
                    if (!englishFile.exists() && !allowNewFileFromScratch) {
                        throw UnexpectedException("English file doesn't exists, I'm unable to create a copy.")
                    }
                    // check folder for language exists or create it
                    if (!otherLanguageFile.parentFile.exists() && !otherLanguageFile.parentFile.mkdirs()) {
                        throw UnexpectedException("There was an error trying to create the folder for language: ${otherLanguageFile.absolutePath}")
                    }

                    if (englishFile.exists()) {
                        // copy eng file
                        Files.copy(englishFile.toPath(), otherLanguageFile.toPath())
                    } else {
                        isNewEmptyFile = true
                    }
                }

                val otherXml = XmlParser(otherLanguageFile, isNewEmptyFile)
                if (isNewEmptyFile) {
                    otherXml.appendElement("resources")
                }
                otherXml.setOrCreateValueTag(itemTagName, itemTagValue)
                otherXml.close()
            }

        }
    }

    private fun getItemsToTranslateCount(): Int {
        return excelManager.getRowCountBySheet(translationSheet)
    }

    private fun getLanguageCounts(): Int {
        return excelManager.getColCountBySheet(translationSheet) - translationFirstLanguage
    }

    override fun close() {
        excelManager.close()
    }
}