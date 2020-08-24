package com.git.kathyBeh.battleShips.view

import com.git.kathyBeh.battleShips.controler.GameController
import com.git.kathyBeh.battleShips.error.ShipPlacementError
import com.git.kathyBeh.battleShips.model.*
import com.git.kathyBeh.battleShips.model.Field
import com.github.michaelbull.result.mapBoth
import javafx.geometry.Insets
import javafx.scene.canvas.Canvas
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

class BattleShipView : View() {
    private val controller: GameController by inject()
    private val size = 10
    private val width = 400.0
    private val height = 400.0
    private val cellHeight = height / size
    private val cellWidth = width / size

    override val root = BorderPane()
    private lateinit var statusLabel: Label

    private lateinit var firstCanvas: Canvas
    private lateinit var secondCanvas: Canvas

    var count = 0

    init {
        title = "Battle Ship"

        with(root) {
            background = Background(
                BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)
            )
            top {
                statusLabel = label("Let the Game Begin!")
                statusLabel.font = Font("Arial", 20.0)
                statusLabel.textFill = Color.web("#1a237e")
            }

            left {
                firstCanvas = drawCanvas()
                placingShipsOnTheField(firstCanvas)
            }

            center {
                vbox(16.0) {
                    button(graphic = ImageView("images/randomButton.png").apply {
                        fitWidth = 120.0
                        fitHeight = 50.0
                    }).action {
                        left {
                            firstCanvas = drawCanvas()
                            controller.playerField = controller.generateAIField()
                            for (sh in controller.playerField.ships) {
                                drawShip(firstCanvas, sh)
                            }
                        }
                        right {
                            secondCanvas = drawCanvas()
                            startGame(secondCanvas)
                        }
                        statusLabel.text = "Let the Game Begin!"
                        statusLabel.textFill = Color.web("#1a237e")
                    }

                    button(graphic = ImageView("images/helpButton.png").apply {
                        fitWidth = 120.0
                        fitHeight = 50.0
                    }).action {
                        HelpWindow().openWindow()
                    }

                    button(graphic = ImageView("images/restartButton.png").apply {
                        fitWidth = 120.0
                        fitHeight = 50.0
                    }).action {
                        controller.playerField = controller.generatePlayerField()
                        controller.computerField = controller.generateAIField()
                        count = 0
                        left {
                            firstCanvas = drawCanvas()
                            placingShipsOnTheField(firstCanvas)
                        }
                        right {
                            secondCanvas = drawCanvas()
                            startGame(secondCanvas)
                        }
                        statusLabel.text = "You have started a new game!"
                        statusLabel.textFill = Color.web("#1a237e")
                    }

                    button(graphic = ImageView("images/closeButton.png").apply {
                        fitWidth = 120.0
                        fitHeight = 50.0
                    }).action {
                        close()
                    }
                    button(graphic = ImageView("images/vikingShip.png").apply {
                        fitWidth = 120.0
                        fitHeight = 100.0
                    })
                }
            }

            right {
                secondCanvas = drawCanvas()
                startGame(secondCanvas)
                }
            }

    }

    private fun drawCanvas(): Canvas {
        return canvas(width, height) {
            val gc = graphicsContext2D
            gc.fill = Color.CORNFLOWERBLUE
            gc.fillRect(0.0, 0.0, width, height)
            gc.stroke = Color.GREEN
            for (shift in 0..size) {
                val x = shift * cellWidth
                val y = shift * cellHeight
                gc.strokeLine(0.0, y, width, y)
                gc.strokeLine(x, 0.0, x, height)
            }
        }
    }

    private fun warnAboutNearAnotherShipPlacement(ship: Ship): String {
        return "Unable to place ship with coordinates: $ship"
    }

    private fun warnAboutOutsideOfFieldPlacement(ship: Ship): String {
        return "Ship is outside of field borders! Coordinates: $ship"
    }

    private fun warnAboutFewShipsPlaced(): String {
        return "You need more ships!"
    }

    internal fun drawResultingShot(field: Field, cell: Cell, shotResult: ShotResult) {
        val canvas = if (field == controller.playerField) {
            firstCanvas
        } else {
            secondCanvas
        }

        if (shotResult == ShotResult.Miss) {
            canvas.graphicsContext2D.fill = Color.DARKBLUE
            canvas.graphicsContext2D.fillOval(
                cell.x * cellWidth + (cellWidth / 4),
                cell.y * cellHeight + (cellHeight / 4),
                cellHeight / 2,
                cellWidth / 2
            )
        } else {
            canvas.graphicsContext2D.fill = Color.BROWN
            canvas.graphicsContext2D.fillRect(cell.x * cellWidth, cell.y * cellHeight, cellHeight, cellWidth)
            canvas.graphicsContext2D.fill = Color.ORANGE
            canvas.graphicsContext2D.fillOval(cell.x * cellWidth, cell.y * cellHeight, cellHeight, cellWidth)
        }
    }

    private fun drawShip(canvas: Canvas, ship: Ship) {
        canvas.graphicsContext2D.fill = Color.BROWN
        for (c in ship.cells) {
            canvas.graphicsContext2D.fillRect(c.x * cellHeight, c.y * cellHeight, cellWidth, cellHeight)
        }
    }

    private fun clickedCell(mouseClicked: MouseEvent): Cell {
        val x = (mouseClicked.x / cellWidth).toInt()
        val y = (mouseClicked.y / cellHeight).toInt()
        return Cell(x, y)
    }

    private fun placingShipsOnTheField(canvas: Canvas) {
        canvas.setOnMouseClicked {
            val clickedCoordinates = clickedCell(it)
            val dir = if (it.button == MouseButton.SECONDARY) {
                Direction.Right
            } else {
                Direction.Down
            }

            val shipLength = controller.shipLengths[count]
            val placement = ShipPlacementDetails(
                shipLength, clickedCoordinates, dir
            )
            val ship = controller.createShip(placement)
            controller.playerField.addShip(ship)
                .mapBoth(success = {
                    count += 1
                    drawShip(firstCanvas, ship)
                }, failure = {
                    statusLabel.text = when (it) {
                        ShipPlacementError.OutsideOfField -> warnAboutOutsideOfFieldPlacement(ship)
                        ShipPlacementError.NearAnotherShip -> warnAboutNearAnotherShipPlacement(ship)
                        else -> "unknown result!"
                    }
                    statusLabel.textFill = Color.web("RED")
                })
            if (count == 10) {
                firstCanvas.setOnMouseClicked { }
            }
        }
    }

    private fun startGame(canvas: Canvas) {
            canvas.setOnMouseClicked {
                if (controller.playerField.howManyShips() == 10) {
                    val shotCoordinates = clickedCell(it)
                    val shotResult = controller.computerField.takeAShot(shotCoordinates)
                    drawResultingShot(controller.computerField, shotCoordinates, shotResult)
                    if (shotResult == ShotResult.Miss) {
                        controller.botShootsUntilMiss()
                    }

                    if (controller.computerField.noMoreAliveShips()) {
                        statusLabel.text = "You won! Please press restart."
                        statusLabel.textFill = Color.web("#1a237e")
                        canvas.setOnMouseClicked { }
                    }
                    if (controller.playerField.noMoreAliveShips()) {
                        statusLabel.text = "Computer won! Please press restart."
                        statusLabel.textFill = Color.web("#1a237e")
                        canvas.setOnMouseClicked { }
                    }
                } else {
                    statusLabel.text = warnAboutFewShipsPlaced()
                    statusLabel.textFill = Color.web("RED")
                }
            }
    }

}