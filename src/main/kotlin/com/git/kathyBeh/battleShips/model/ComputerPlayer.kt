package com.git.kathyBeh.battleShips.model

import java.util.concurrent.ThreadLocalRandom

class AIPlayer {
    private val alreadyTakenShots = mutableSetOf<Cell>()
    private var shipDirection = Direction.RIGHT

    internal fun shootRandomly(): Cell {
        var shot = Cell(getCoordinate(), getCoordinate())
        while (shot in alreadyTakenShots) {
            shot = Cell(getCoordinate(), getCoordinate())
        }
        alreadyTakenShots.add(shot)
        return shot
    }

    internal fun shootNear(cell: Cell): Cell {
        val shiftedCoordinate: Cell
        when (shipDirection) {
            Direction.RIGHT -> {
                shiftedCoordinate = Cell(cell.x + 1, cell.y)
                if (shiftedCoordinate in alreadyTakenShots || shiftedCoordinate.x > 9) {
                    shipDirection = Direction.DOWN
                    shootNear(cell)
                }
            }
            Direction.DOWN -> {
                shiftedCoordinate = Cell(cell.x, cell.y + 1)
                if (shiftedCoordinate in alreadyTakenShots || shiftedCoordinate.y > 9) {
                    shipDirection = Direction.LEFT
                    shootNear(cell)
                }
            }
            Direction.LEFT -> {
                shiftedCoordinate = Cell(cell.x - 1, cell.y)
                if (shiftedCoordinate in alreadyTakenShots || shiftedCoordinate.x < 0) {
                    shipDirection = Direction.UP
                    shootNear(cell)
                }
            }
            Direction.UP -> {
                shiftedCoordinate = Cell(cell.x, cell.y - 1)
                if (shiftedCoordinate in alreadyTakenShots || shiftedCoordinate.y < 0) {
                    shipDirection = Direction.RIGHT
                    shootNear(cell)
                }
            }
        }
        alreadyTakenShots.add(shiftedCoordinate)
        return shiftedCoordinate
    }

    private fun getCoordinate(): Int =
        ThreadLocalRandom.current().nextInt(0, 10)

    internal fun shipHalo (ship: Ship) =
            alreadyTakenShots.addAll(ship.haloShip())

}