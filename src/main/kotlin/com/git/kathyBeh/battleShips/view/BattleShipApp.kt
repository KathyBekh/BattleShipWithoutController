package com.git.kathyBeh.battleShips.view

import javafx.application.Application
import tornadofx.App

class BattleShipApp : App(BattleShipView::class) {
}

fun main(args: Array<String>) {
    Application.launch(BattleShipApp::class.java, *args)
}




