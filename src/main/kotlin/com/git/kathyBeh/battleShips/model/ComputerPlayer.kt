package com.git.kathyBeh.battleShips.model

import java.util.concurrent.ThreadLocalRandom

class AIPlayer {
    private val shoots = mutableSetOf<Cell>()

    fun shoot(): Cell {
        var shoot = Cell(getCoordinate(), getCoordinate())
        while (shoot in shoots) {
            shoot = Cell(getCoordinate(), getCoordinate())
        }
        shoots.add(shoot)
        return shoot
    }

    private fun getCoordinate(): Int =
        ThreadLocalRandom.current().nextInt(0, 10)
}