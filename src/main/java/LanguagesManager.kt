import com.sun.javaws.exceptions.InvalidArgumentException
import java.io.File

/**
 * Created by juan.saravia on 04/07/2016.
 */
class LanguagesManager(excelFile: File) {

    private val excelManager: ExcelManager
    private val basePath: String
    private var languages: MutableMap<String, String> = mutableMapOf()

    private val configSheet = 0
    private val configBasePathRow = 0
    private val configBasePathCell = 1
    private val languageId = "LanguageID"

    private val translationSheet = 1
    private val translationFirstCellLanguagePos = 3 // TODO: Make it dynamic
    private val translationRelativePathPos = 0
    private val translationFileNamePos = 1
    private val translationTagNamePos = 2

    constructor(excelPath: String) : this(File(excelPath))

    init {
        excelManager = ExcelManager(excelFile)
        basePath = excelManager.getStringValue(configSheet, configBasePathRow, configBasePathCell)
        initLanguages()
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
        val itemIndex = 0
        val rowIndex = 1
        val languageType = excelManager.getStringValue(translationSheet, 0, translationFirstCellLanguagePos)
        val languageFolderName = languages[languageType]

        val itemRelativePath = excelManager.getStringValue(translationSheet, rowIndex, translationRelativePathPos)
        val itemFileName = excelManager.getStringValue(translationSheet, rowIndex, translationFileNamePos)
        val itemTagName = excelManager.getStringValue(translationSheet, rowIndex, translationTagNamePos)

        val englishFile = File(basePath + itemRelativePath + "\\values\\" + itemFileName)
        val otherLanguageFile = File(basePath + itemRelativePath + "\\values-$languageType\\" + itemFileName)
        if (otherLanguageFile == null) {
            // TODO: create file from english
        }
    }
}