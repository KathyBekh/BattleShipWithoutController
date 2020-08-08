
import javafx.application.Application
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.*
import tornadofx.*

//class NTText  {
//    fun printText() {
//        val t = Text()
//        t.setFont(Font(20.0))
//        t.setWrappingWidth(200.0)
//        t.setTextAlignment(TextAlignment.JUSTIFY)
//        t.setText("The quick brown fox jumps over the lazy dog")
//    }
//}

class ViewTest : View() {
    override val root: StackPane = StackPane()
    init {
        title = "Game rules"
        with(root) {
            val t = label ("это правила игры они очень длинные. Я пытвюсь научиться их отображать по запросу" +
                    "Это жутко сложно! \n Консольные приложения в этом плане удобнее. Столько всего перепробовала! " +
                    "что в голове уже каша. Не понимаю что и как работает! \n SOS! \n HELP ME!")

        }
//        val t = Text()
//        t.setFont(Font(20.0))
//        t.setWrappingWidth(200.0)
//        t.setTextAlignment(TextAlignment.JUSTIFY)
//        t.setFill(Color.RED)
//        t.setText("The quick brown fox jumps over the lazy dog")
//        println(t)
    }
}
class AppText: App(ViewTest:: class){
}
fun main(args: Array<String>) {
    launch<AppText>(args)
}