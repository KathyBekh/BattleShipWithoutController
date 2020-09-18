package com.git.kathyBeh.battleShips.model

import kotlin.random.Random

class AIPlayer {
    private val possibleShots = addPossibleShots().toMutableSet()
    private var woundedShip = mutableListOf<Cell>()

    internal fun shootNear(): Cell {
        var shot: Cell
        if (woundedShip.size == 1) {
            shot = nextRandomCellFromNearest()
        } else {
            var n = 0
            do {
                shot = choose(woundedShip[n])
                n += 1
            } while (shot !in possibleShots)
        }

        possibleShots.remove(shot)
        return shot
    }

    internal fun shootRandomly(): Cell {
        var shot = Cell(getCoordinate(), getCoordinate())
        while (shot !in possibleShots) {
            shot = Cell(getCoordinate(), getCoordinate())
        }
        possibleShots.remove(shot)
        return shot
    }

    private fun getCoordinate(): Int =
        Random.nextInt(10)

    internal fun shipHalo (ship: Ship) =
            possibleShots.removeAll(ship.haloShip())

    private fun addPossibleShots(): Set<Cell> {
        val possiblyShots = mutableSetOf<Cell>()
        for (x in 0 until 10) {
            for (y in 0 until 10) {
                possiblyShots.add(Cell(x, y))
            }
        }
        return possiblyShots
    }

    internal fun addWoundedShip(cell: Cell) {
        woundedShip.add(cell)
    }

    internal fun killShip(cell: Cell): Ship {
        return if (woundedShip.isEmpty()) {
            Ship(listOf(cell))
        } else {
            woundedShip.add(cell)
            Ship(woundedShip)
        }
    }

    internal fun removeShipFromWoundedShipAfterKillIt() {
        woundedShip.clear()
    }

    internal fun noWoundedShip(): Boolean {
        return woundedShip.isEmpty()
    }

    // Метод случайным образом выбирает 1 из 4 клеток, вокруг раненой пвлубы корабля.
    private fun randomCellFromTheNearest(cell: Cell): Cell {
        return when (Random.nextInt(4)) {
            0 -> Cell(cell.x + 1, cell.y)
            1 -> Cell(cell.x - 1, cell.y)
            2 -> Cell(cell.x, cell.y + 1)
            else -> Cell(cell.x, cell.y - 1)
        }
    }

    // Метод вызывает метод randomCellFromTheNearest до тех пор пока не найдётся
    // клетка(рядом с раненой "палубой" корабля) в которую ещё не стреляли.
    private fun nextRandomCellFromNearest(): Cell {
        var shot = randomCellFromTheNearest(woundedShip[0])
        while (shot !in possibleShots) {
            shot = randomCellFromTheNearest(woundedShip[0])
        }
        return shot
    }

    private fun shootY(cell: Cell): Cell {
        var shot = (Cell(cell.x, cell.y + 1))
        if (shot !in possibleShots) {
            shot = (Cell(cell.x, cell.y - 1))
        }
        return shot
    }

    private fun shootX(cell: Cell): Cell {
        var shot = (Cell(cell.x + 1, cell.y))
            if (shot !in possibleShots) {
                shot = (Cell(cell.x - 1, cell.y))
            }
        return shot
    }

    private fun directionWoundedShip(): Direction {
        return if (woundedShip[0].x == woundedShip[1].x) {
                Direction.DOWN
            } else { Direction.RIGHT }
    }

    private fun choose(cell: Cell): Cell {
        return if (directionWoundedShip() == Direction.RIGHT) {
            shootX(cell)
        }
        else {
            shootY(cell)
        }
    }

}