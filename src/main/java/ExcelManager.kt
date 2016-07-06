import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

/**
 * Created by juan.saravia on 04/07/2016.
 */
class ExcelManager : AutoCloseable {
    private lateinit var fis: FileInputStream
    private lateinit var wb: XSSFWorkbook
    private lateinit var formatter: DataFormatter
    private var symbols: MutableMap<String, String> = HashMap()

    private fun init() {
        try {
            wb = XSSFWorkbook(fis)
            formatter = DataFormatter()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        symbols = HashMap<String, String>(1)
        symbols.put("®", "\\u00AE")
        symbols.put("&", "&amp;")
    }

    constructor(file: File) {
        try {
            fis = FileInputStream(file)
            init()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    constructor(filePath: String) {
        try {
            fis = FileInputStream(File(filePath))
            init()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    val titleName: String
        get() {
            val sheet = wb.getSheetAt(0)
            val row = sheet.getRow(1)
            val cell = row.getCell(1, XSSFRow.RETURN_BLANK_AS_NULL)
            var value = formatter.formatCellValue(cell)
            for (symbol in symbols.entries) {
                value = value.replace(symbol.key, symbol.value)
            }
            return value
        }

    @Throws(IOException::class)
    override fun close() {
        wb.close()
        fis.close()
    }

    fun getStringValue(sheet: Int, row: Int, cell: Int): String {
        val cellValue = getSheet(sheet).getRow(row)?.getCell(cell, XSSFRow.RETURN_BLANK_AS_NULL) ?: return ""
        return formatter.formatCellValue(cellValue)
    }

    fun getSheet(position: Int): XSSFSheet {
        return wb.getSheetAt(position)
    }

    fun getRowPosition(sheet: Int, valueFirstCell: String): Int {
        wb.getSheetAt(sheet).rowIterator().withIndex().forEach { row ->
            if (valueFirstCell == formatter.formatCellValue(row.value.getCell(0, XSSFRow.RETURN_BLANK_AS_NULL))) {
                return row.index
            }
        }

        return 0
    }

    fun getRowCountBySheet(sheet: Int): Int {
        // TODO: move this to constructor as it's always the same value
        wb.getSheetAt(sheet).rowIterator().withIndex().forEach { row ->
            if (formatter.formatCellValue(row.value.getCell(0, XSSFRow.RETURN_BLANK_AS_NULL)).isEmpty()) {
                return row.index
            }
        }
        return 0
    }

    fun getColCountBySheet(sheet: Int): Int {
        // TODO: move this to constructor as it's always the same value
        var count = 0
        while (!wb.getSheetAt(sheet).getRow(0).getCell(count).stringCellValue.isEmpty()) {
            count++
        }
        return count
    }
}
