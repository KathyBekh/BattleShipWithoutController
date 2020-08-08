package com.git.kathyBeh.battleShips.view

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.layout.Priority
import tornadofx.*

class ChoosePlayerDialog : Dialog<ButtonType>() {
    private val bluePlayer = SimpleStringProperty()
    val blueComputer: Boolean get() = bluePlayer.value == "Computer"

    private val redPlayer = SimpleStringProperty()
    val redComputer: Boolean get() = redPlayer.value == "Computer"

    init {
        title = "Battle_Ships"
        with (dialogPane) {
            headerText = "Choose players"
            buttonTypes.add(ButtonType("Start Game", ButtonBar.ButtonData.OK_DONE))
            buttonTypes.add(ButtonType("Quit", ButtonBar.ButtonData.CANCEL_CLOSE))
            content = hbox {
                vbox {
                    label("Blue")
                    togglegroup {
                        bind(bluePlayer)
                        radiobutton("Human") {
                            isSelected = true
                        }
                        radiobutton("Computer")
                    }
                }
                spacer(Priority.ALWAYS)
                vbox {
                    label("Red")
                    togglegroup {
                        bind(redPlayer)
                        radiobutton("Human")
                        radiobutton("Computer") {
                            isSelected = true
                        }
                    }
                }
            }
        }
    }
}