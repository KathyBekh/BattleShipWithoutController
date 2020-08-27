package com.git.kathyBeh.battleShips.model

import java.util.concurrent.ThreadLocalRandom

class AIPlayer {
    private val alreadyTakenShots = mutableSetOf<Cell>()

    fun shoot(): Cell {
        var shot = Cell(getCoordinate(), getCoordinate())
        while (shot in alreadyTakenShots) {
            shot = Cell(getCoordinate(), getCoordinate())
        }
        alreadyTakenShots.add(shot)
        return shot
    }

    private fun getCoordinate(): Int =
        ThreadLocalRandom.current().nextInt(0, 10)

    fun shipHalo (ship: Ship) =
            alreadyTakenShots.addAll(ship.haloShip())

}