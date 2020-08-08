import javafx.stage.StageStyle
import tornadofx.*

class MyView : View() {
    override val root = vbox {
        button("Press Me") {
            action {
                find<MyFragment>().openModal(stageStyle = StageStyle.UTILITY)
            }
        }
    }
}

class MyFragment: Fragment() {
    override val root = label("This is a popup")
}
