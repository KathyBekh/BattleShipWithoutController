package com.git.kathyBeh.battleShips.controler

import com.git.kathyBeh.battleShips.model.*
import com.git.kathyBeh.battleShips.view.BattleShipView
import com.github.michaelbull.result.onSuccess
import tornadofx.Controller
import kotlin.random.Random

class GameController: Controller() {
    private val view: BattleShipView by inject()
    internal val shipLengths = listOf(4, 3, 3, 2, 2, 2, 1, 1, 1, 1)
    internal var playerField: Field = generatePlayerField()
    internal var computerField: Field = generateAIField()
    private val bot = AIPlayer()

    internal fun generatePlayerField(): Field {
        return Field(10, 10)
    }

    private fun randomCellCoordinate(): Int =
        Random.nextInt(10)  // рандомная ячейка нужна для раставления кораблей и  "выстрела" АИ.

    private fun randomDirection(): Direction =
        if (Random.nextBoolean()) Direction.Down    // рандомное направление нужно для раставления кораблей АИ.
        else Direction.Right

//    Метод генеирует игровое поле АИ и заполняет его кораблями рандомно.

    internal fun generateAIField(): Field {
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

    internal fun createShip(placement: ShipPlacementDetails): Ship =
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

    internal fun botShootsUntilMiss() {
        do {
            val shotCoordinates = bot.shoot()
            val shotResult = playerField.takeAShot(shotCoordinates)
            view.drawResultingShot(playerField, shotCoordinates, shotResult)
        } while (!playerField.noMoreAliveShips() && shotResult != ShotResult.Miss)
    }

}