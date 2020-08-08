package com.git.kathyBeh.battleShips.view

import com.git.kathyBeh.battleShips.controler.BoardBasedCellListener
import com.git.kathyBeh.battleShips.model.*
import com.git.kathyBeh.battleShips.model.Field
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.concurrent.timer

class BattleShipView : View(), BoardListener {

    private data class AutoTurnEvent(val player: ComputerPlayer) : FXEvent()

    private val columnsNumber = 10

    private val rowsNumber = 10

    private val board = Field(columnsNumber, rowsNumber)

    private var blueComputer =
        if ((app as BattleShipApp).blueHuman) null else ComputerPlayer(board)

    private var redComputer =
        if ((app as BattleShipApp).redHuman) null else ComputerPlayer(board)

    private val computerToMakeTurn: ComputerPlayer?
        get() = if (board.turn == Ship.BLUE) blueComputer else redComputer

    private val buttons = mutableMapOf<Cell, Button>()

    private var inProcess = true

    private lateinit var statusLabel: Label

    override val root = BorderPane()

    init {
        title = "Battle Ship"
        val listener = BoardBasedCellListener(board)
        board.registerListener(this)

        with (root) {
            top {
                vbox {
                    menubar {
                        menu("Game") {
                            item("Restart").action {
                                restartGame()
                            }
                            item("Configure & restart").action {
                                reconfigureGame()
                            }
                            separator()
                            item("Exit").action {
                                this@BattleShipView.close()
                            }
                        }
                    }
                    toolbar {
//                  //ImageView iv = new ImageView(getClass().getResource("za.jpg").toExternalForm())
                        button(graphic = ImageView("images/restart.png").apply {
                            fitWidth = 16.0
                            fitHeight = 16.0
                        }).action {
                            restartGame()
                        }
                    }
                    radiobutton {
                        statusLabel = label("hor")
                    }

                    radiobutton {
                        statusLabel = label("ver")
                    }
                }
            }
            right {
                gridpane {
                    hgap = 5.0
                    vgap = 5.0
                    val dimension = Dimension(64.0, Dimension.LinearUnits.px)
                    for (row in 0 until rowsNumber) {
                        row {
                            for (column in 0 until columnsNumber) {
                                val cell = Cell(column, rowsNumber - 1 - row)
                                val button = button {
                                    style {
                                        backgroundColor += Color.CADETBLUE
                                        minWidth = dimension
                                        minHeight = dimension
                                    }
                                }
                                button.action {
                                    if (inProcess) {
                                        listener.cellClicked(cell)
                                    }
                                }
                                buttons[cell] = button
                            }
                        }
                    }
                }
            }

            bottom {
                statusLabel = label("")
            }

            subscribe<AutoTurnEvent> {
                it.player.makeComputerTurn()
            }
        }

        updateBoardAndStatus()
        startTimerIfNeeded()
    }

    private fun restartGame() {
        board.clear()
        for (x in 0 until columnsNumber) {
            for (y in 0 until rowsNumber) {
                updateBoardAndStatus(Cell(x, y))
            }
        }
        inProcess = true
        startTimerIfNeeded()
    }

    private fun reconfigureGame() {
        val dialog = ChoosePlayerDialog()
        val result = dialog.showAndWait()
        if (result.isPresent && result.get().buttonData == ButtonBar.ButtonData.OK_DONE) {
            blueComputer = if (dialog.blueComputer) ComputerPlayer(board) else null
            redComputer = if (dialog.redComputer) ComputerPlayer(board) else null
            restartGame()
        } else {
            close()
        }
    }

    private fun startTimerIfNeeded() {
        if (blueComputer != null || redComputer != null) {
            timer(daemon = true, period = 1000) {
                if (inProcess) {
                    computerToMakeTurn?.let {
                        fire(AutoTurnEvent(it))
                    }
                } else {
                    this.cancel()
                }
            }
        }
    }

    override fun turnMade(cell: Cell) {
        updateBoardAndStatus(cell)
    }

    private fun updateBoardAndStatus(cell: Cell? = null) {
        val winningCombo = board.winningCombo()
        val winner = winningCombo?.winner
        statusLabel.text = when {
            !board.hasFreeCells() -> {
                inProcess = false
                "Draw! Press 'Restart' to continue"
            }
            winner == Ship.BLUE -> {
                inProcess = false
                "Blue win! Press 'Restart' to continue"
            }
            winner == Ship.RED -> {
                inProcess = false
                "Reds win! Press 'Restart' to continue"
            }
            board.turn == Ship.BLUE ->
                "Game in process: Blue turn"
            else ->
                "Game in process: Reds turn"
        }
        if (cell == null) return
        val ship = board[cell]
        buttons[cell]?.apply {
            graphic = circle(radius = 24.0) {
                fill = when (ship) {
                    null -> Color.CADETBLUE
                    Ship.BLUE -> Color.BLUE
                    Ship.RED -> Color.RED
                }
            }
        }
        if (winner != null) {
            val startCell = winningCombo.startCell
            val endCell = winningCombo.endCell
            var currentCell = startCell
            while (true) {
                buttons[currentCell]?.apply {
                    graphic = circle(radius = 12.0) {
                        fill = when (winner) {
                            Ship.BLUE -> Color.BLUE
                            Ship.RED -> Color.RED
                        }
                    }
                }
                if (currentCell == endCell) break
                currentCell = currentCell.plus(winningCombo.direction)
            }
        }
    }

    private fun ComputerPlayer.makeComputerTurn() {
        val turn = bestTurn(2)
        val x = turn.turn
        if (x != null) {
            board.makeTurn(x)
        }
    }

}