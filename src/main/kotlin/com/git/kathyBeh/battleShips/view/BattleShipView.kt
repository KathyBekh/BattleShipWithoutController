package com.git.kathyBeh.battleShips.view

import com.git.kathyBeh.battleShips.error.ShipPlacementError
import com.git.kathyBeh.battleShips.model.*
import com.git.kathyBeh.battleShips.model.Field
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.onSuccess
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
import tornadofx.*
import kotlin.random.Random

class BattleShipView : View() {
    private val size = 10
    private val width = 400.0
    private val height = 400.0
    private val cellHeight = height / size
    private val cellWidth = width / size

    override val root = BorderPane()
    private var statusLabel: Label = label("Print text")

    private val shipLengths = listOf(4, 3, 3, 2, 2, 2, 1, 1, 1, 1)
    private var playerField: Field = generatePlayerField()
    private var computerField: Field = generateAIField()
    private val bot = AIPlayer()

    private lateinit var firstCanvas: Canvas
    private lateinit var secondCanvas: Canvas

    var count = 0

    init {
        title = "Battle Ship"

        with(root) {
            background = Background(
                BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)
            )
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
                            playerField = generateAIField()
                            for (sh in playerField.ships) {
                                drawShip(firstCanvas, sh)
                            }
                        }
                        right {
                            secondCanvas = drawCanvas()
                            startGame(secondCanvas)
                        }
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
                        playerField = generatePlayerField()
                        computerField = generateAIField()
                        count = 0
                        left {
                            firstCanvas = drawCanvas()
                            placingShipsOnTheField(firstCanvas)
                        }
                        right {
                            secondCanvas = drawCanvas()
                            startGame(secondCanvas)
                        }
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

    private fun generatePlayerField(): Field {
        return Field(size, size)
    }

// Метод создает корабль в указанном месте нужного размера.

    private fun createShip(placement: ShipPlacementDetails): Ship =
        when (placement.direction) {
            Direction.Right -> {
                val start = placement.initialCell.x
                Ship((start until start + placement.length).map { Cell(it, placement.initialCell.y) }.toList())
            }
            Direction.Down -> {
                val start = placement.initialCell.y
                Ship((start until start + placement.length).map { Cell(placement.initialCell.x, it) }.toList())
            }
        }

    private fun randomCellCoordinate(): Int =
        Random.nextInt(10)  // рандомная ячейка нужна для раставления кораблей и  "выстрела" АИ.

    private fun randomDirection(): Direction =
        if (Random.nextBoolean()) Direction.Down    // рандомное направление нужно для раставления кораблей АИ.
        else Direction.Right

//    Метод генеирует игровое поле АИ и заполняет его кораблями рандомно.

    private fun generateAIField(): Field {
        val aiField = Field(10, 10)
        for (shipLength in shipLengths) {
            var shipPlaced = false
            while (!shipPlaced) {
                val ship = createShip(
                    ShipPlacementDetails(
                        shipLength,
                        Cell(randomCellCoordinate(), randomCellCoordinate()),
                        randomDirection()
                    )
                )
                aiField.addShip(ship).onSuccess {
                    shipPlaced = true
                }
            }
        }
        return aiField
    }

    private fun botShootsUntilMiss() {
        do {
            val shotCoordinates = bot.shoot()
            val shotResult = playerField.takeAShot(shotCoordinates)
            drawResultingShot(playerField, shotCoordinates, shotResult)
        } while (!playerField.noMoreAliveShips() && shotResult != ShotResult.Miss)
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

    private fun warnAboutNearAnotherShipPlacement(ship: Ship) {
        println("Unable to place ship with coordinates: $ship")
    }

    private fun warnAboutOutsideOfFieldPlacement(ship: Ship) {
        println("Ship is outside of field borders! Coordinates: $ship")
    }

    private fun warnAboutFewShipsPlaced() {
        println("You need more ships!")
    }

    private fun drawResultingShot(field: Field, cell: Cell, shotResult: ShotResult) {
        val canvas = if (field == playerField) {
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

            val shipLength = shipLengths[count]
            val placement = ShipPlacementDetails(
                shipLength, clickedCoordinates, dir
            )
            val ship = createShip(placement)
            playerField.addShip(ship)
                .mapBoth(success = {
                    count += 1
                    drawShip(firstCanvas, ship)
                }, failure = {
                    when (it) {
                        ShipPlacementError.OutsideOfField -> warnAboutOutsideOfFieldPlacement(ship)
                        ShipPlacementError.NearAnotherShip -> warnAboutNearAnotherShipPlacement(ship)
                    }
                })
            if (count == 10) {
                firstCanvas.setOnMouseClicked { }
            }
        }
    }

    private fun startGame(canvas: Canvas) {
            canvas.setOnMouseClicked {
                if (playerField.howManyShips() == 10) {
                    val shotCoordinates = clickedCell(it)
                    val shotResult = computerField.takeAShot(shotCoordinates)
                    drawResultingShot(computerField, shotCoordinates, shotResult)
                    if (shotResult == ShotResult.Miss) {
                        botShootsUntilMiss()
                    }

                    if (computerField.noMoreAliveShips()) {
                        println("You won! Please press restart.")
                        canvas.setOnMouseClicked { }
                    }
                    if (playerField.noMoreAliveShips()) {
                        println("Computer won! ")
                        canvas.setOnMouseClicked { }
                    }
                } else {
                    warnAboutFewShipsPlaced()
                }
            }
    }
}