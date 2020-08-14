package com.git.kathyBeh.battleShips.model

class Ship(val cells: List<Cell>) {
    private var aliveCells = cells.toMutableList()

    fun doesFitInField(width: Int, height: Int): Boolean = cells.all {
        it.x < width && it.y < height
    }

    fun canPlaceNear(ship: Ship): Boolean = ship.cells.all {
        canPlaceNear(it)
    }

    private fun canPlaceNear(cell: Cell): Boolean = cells.all {
        it.canPlaceNear(cell)
    }

    fun takeAShot(coordinates: Cell): ShotResult {
        if (coordinates in aliveCells) {
            aliveCells.remove(coordinates)
            return if (aliveCells.isEmpty()) ShotResult.Kill else ShotResult.Hit
        }
        return ShotResult.Miss
    }

    override fun toString(): String {
        return cells.joinToString()
    }
}