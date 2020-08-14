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
    private val firstField: Field = generatePlayerField()
    private val secondField: Field = generateAIField()
    private val firstPlayer: Player = HumanPlayer()
    private val secondPlayer: Player = AIPlayer()

    private lateinit var firstCanvas: Canvas
    private lateinit var secondCanvas: Canvas

    private var clickCoordinate = Cell(10, 10)
    private var shotResult = ShotResult.Miss

    init {
        title = "Battle Ship"

        with(root) {
            background = Background(
                BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)
            )
            left {
                firstCanvas = drawCanvas()
                for (sh in firstField.ships) {
                    drawShip(firstCanvas, sh)
                }
            }
            center {
                toolbar {

                    button(graphic = ImageView("images/shipsWheel1.png").apply {
                        fitWidth = 100.0
                        fitHeight = 100.0
                    }).action {
                        startGame()
                        println("You start a new game! Hahahahah")
                    }
                }
            }
            right {
                secondCanvas = drawCanvas()

                secondCanvas.setOnMouseClicked {
                    val x = (it.x / cellWidth).toInt()
                    val y = (it.y / cellHeight).toInt()
                    clickCoordinate = Cell(x, y)

                    drawResultingShot(secondField, clickCoordinate, shotResult)
                    drawResultingShot(firstField, secondPlayer.shoot(), shotResult)
                }
            }
            bottom {
                statusLabel
            }
        }

    }

    private fun generatePlayerField(): Field {
        val playerField = Field(10, 10)
        for (shipLength in shipLengths) {
            var shipPlaced = false
            while (!shipPlaced) {
                val placement = ShipPlacementDetails(
                    shipLength,
                    Cell(randomCellCoordinate(), randomCellCoordinate()),
                    randomDirection()
                )
                val ship = createShip(placement)
                playerField.addShip(ship)
                    .mapBoth(success = {
                        shipPlaced = true
//                        view.drawShip(view.drawCanvas(), ship)
                    }, failure = {
                        when (it) {
                            ShipPlacementError.OutsideOfField -> warnAboutOutsideOfFieldPlacement(ship)
                            ShipPlacementError.NearAnotherShip -> warnAboutNearAnotherShipPlacement(ship)
                        }
                    })
            }
        }
        return playerField
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

    private fun startGame() {
        while (!secondField.noMoreAliveShips() && !firstField.noMoreAliveShips()) {
            shootUntilMiss(firstPlayer, secondField)
            shootUntilMiss(secondPlayer, firstField)
        }
    }

    private fun shootUntilMiss(player: Player, enemyField: Field) {
        do {
            shotResult = enemyField.takeAShot(player.shoot())
            statusLabel.text = shotResult.toString()
        } while (!enemyField.noMoreAliveShips() && shotResult != ShotResult.Miss)
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
        statusLabel.text = "Unable to place ship with coordinates: $ship"
    }

    private fun warnAboutOutsideOfFieldPlacement(ship: Ship) {
        statusLabel.text = "Ship is outside of field borders! Coordinates: $ship"
    }

    fun askForShot(): Cell {
        return clickCoordinate
    }

    private fun drawResultingShot(field: Field, cell: Cell, shotResult: ShotResult) {
        val canvas = if (field == firstField) {
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
}