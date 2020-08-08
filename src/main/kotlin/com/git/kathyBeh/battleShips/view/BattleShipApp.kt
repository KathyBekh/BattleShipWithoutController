package com.git.kathyBeh.battleShips.view

import javafx.application.Application
import javafx.scene.control.ButtonBar
import javafx.stage.Stage
import tornadofx.App

class BattleShipApp : App(BattleShipView::class) {

    var blueHuman = true

    var redHuman = true

    /**
     * Sets stage with the scene.
     */
    override fun start(stage: Stage) {
        //Sets up dialog before main application.
        val dialog = ChoosePlayerDialog()
        //Retrieves response value.
        val result = dialog.showAndWait()
        if (result.isPresent && result.get().buttonData == ButtonBar.ButtonData.OK_DONE) {
            blueHuman = !dialog.blueComputer
            redHuman = !dialog.redComputer
            super.start(stage)
        }
    }

}

fun main(args: Array<String>) {
    Application.launch(BattleShipApp::class.java, *args)
}




