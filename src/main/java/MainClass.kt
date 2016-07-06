/**
 * Created by juan.saravia on 04/07/2016.
 */
object MainClass {

    @JvmStatic fun main(args: Array<String>) {
        val lng = LanguageTranslator(args[0])
        lng.translate()
    }
}
