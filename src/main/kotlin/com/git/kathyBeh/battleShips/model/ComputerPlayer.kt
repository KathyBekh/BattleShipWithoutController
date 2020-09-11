package com.git.kathyBeh.battleShips.model

import java.util.concurrent.ThreadLocalRandom

class AIPlayer {
    private val alreadyTakenShots = mutableSetOf<Cell>()
    private var shipDirection = Direction.RIGHT

    internal fun shoot(): Cell {
        var shot = Cell(getCoordinate(), getCoordinate())
        while (shot in alreadyTakenShots) {
            shot = Cell(getCoordinate(), getCoordinate())
        }
        alreadyTakenShots.add(shot)
        return shot
    }

    internal fun shootNear(cell: Cell): Cell {
        val shot: Cell
        when (shipDirection) {
            Direction.RIGHT -> {
                shot = Cell(cell.x + 1, cell.y)
                if (shot in alreadyTakenShots || cell.x > 9) {
                    shipDirection = Direction.DOWN
                    shootNear(cell)
                }
            }
            Direction.DOWN -> {
                shot = Cell(cell.x, cell.y + 1)
                if (shot in alreadyTakenShots || cell.y > 9) {
                    shipDirection = Direction.LEFT
                    shootNear(cell)
                }
            }
            Direction.LEFT -> {
                shot = Cell(cell.x - 1, cell.y)
                if (shot in alreadyTakenShots || cell.x < 0) {
                    shipDirection = Direction.UP
                    shootNear(cell)
                }
            }
            Direction.UP -> {
                shot = Cell(cell.x, cell.y - 1)
                if (shot in alreadyTakenShots || cell.y < 0) {
                    shipDirection = Direction.RIGHT
                    shootNear(cell)
                }
            }
        }
        alreadyTakenShots.add(shot)
        return shot
    }

    private fun getCoordinate(): Int =
        ThreadLocalRandom.current().nextInt(0, 10)

    internal fun shipHalo (ship: Ship) =
            alreadyTakenShots.addAll(ship.haloShip())

}