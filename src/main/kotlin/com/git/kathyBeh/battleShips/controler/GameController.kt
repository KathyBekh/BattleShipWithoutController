package com.git.kathyBeh.battleShips.controler

import com.git.kathyBeh.battleShips.model.*
import com.git.kathyBeh.battleShips.view.BattleShipView
import com.github.michaelbull.result.onSuccess
import tornadofx.Controller
import kotlin.random.Random

class GameController : Controller() {
    private val view: BattleShipView by inject()
    internal val shipLengths = listOf(4, 3, 3, 2, 2, 2, 1, 1, 1, 1)
    internal var playerField: Field = generatePlayerField()
    internal var computerField: Field = generateAIField()
    private var bot = AIPlayer()

    private fun generatePlayerField(): Field {
        return Field(10, 10)
    }

    private fun randomCellCoordinate(): Int =
        Random.nextInt(10)  // рандомная ячейка для раставления кораблей и "выстрела" ИИ.

    private fun randomDirection(): Direction =
        if (Random.nextBoolean()) Direction.DOWN // рандомное направление для раставления кораблей ИИ.
        else Direction.RIGHT

    // Метод генерирует игровое поле ИИ и заполняет его кораблями рандомно.
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
            Direction.RIGHT -> {
                val start = placement.initialCell.x
                Ship((start until start + placement.length).map { Cell(it, placement.initialCell.y) }.toList())
            }
            Direction.DOWN -> {
                val start = placement.initialCell.y
                Ship((start until start + placement.length).map { Cell(placement.initialCell.x, it) }.toList())
            }
            Direction.LEFT -> {
                val start = placement.initialCell.x
                Ship((start until start - placement.length).map { Cell(it, placement.initialCell.y) }.toList())
            }
            Direction.UP -> {
                val start = placement.initialCell.y
                Ship((start until start - placement.length).map { Cell(placement.initialCell.x, it) }.toList())
            }
        }

    private var woundedShip: Cell? = null

    internal fun botShootsUntilMiss() {
        do {
            val shotCoordinates = shootAsBot()
            val shotResult = playerField.takeAShot(shotCoordinates)
            when (shotResult) {
                ShotResult.MISS -> {
                }
                ShotResult.HIT -> {
                    woundedShip = shotCoordinates
                }
                ShotResult.KILL -> {
                    woundedShip = null
                    val killSh = playerField.killShip(shotCoordinates)
                    if (killSh != null) {
                        bot.shipHalo(killSh)
                    }
                }
            }
            view.drawResultingShot(playerField, shotCoordinates, shotResult)
        } while (!playerField.noMoreAliveShips() && shotResult != ShotResult.MISS)
    }


    private fun shootAsBot(): Cell =
        if (woundedShip == null) {
            bot.shootRandomly()
        } else {
            bot.shootNear(woundedShip!!)
        }

    internal fun newPlayers() {
        playerField = generatePlayerField()
        computerField = generateAIField()
        bot = AIPlayer()
    }
}