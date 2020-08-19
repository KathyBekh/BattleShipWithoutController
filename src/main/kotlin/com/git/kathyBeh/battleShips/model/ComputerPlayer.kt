package com.git.kathyBeh.battleShips.model

import java.util.concurrent.ThreadLocalRandom

class AIPlayer {
    fun shoot(): Cell {
        return Cell(getCoordinate(), getCoordinate())
    }

    private fun getCoordinate(): Int =
        ThreadLocalRandom.current().nextInt(0, 10)
}