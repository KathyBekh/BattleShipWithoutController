package com.git.kathyBeh.battleShips.model


import java.util.*


/**
 * Разработать программу с графическим интерфейсом, реализующую указанную в
задании функциональность, на языках Java и/или Kotlin. При проектировании
использовать объектно-ориентированный подход. Разработать автоматические тесты
для отдельных частей программы. Написанный код держать в отдельном проекте или
модуле в репозитории на GitHub.
 */

import com.git.kathyBeh.battleShips.error.ShipPlacementError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result


class Field(private val width: Int, private val height: Int) {
    val ships = mutableListOf<Ship>()
    private val aliveShips = mutableListOf<Ship>()

    fun addShip(ship: Ship): Result<Unit, ShipPlacementError> {
        if (!isInsideField(ship)) {
            return Err(ShipPlacementError.OutsideOfField)
        }
        if (!canAddShip(ship)) {
            return Err(ShipPlacementError.NearAnotherShip)
        }
        ships.add(ship)
        aliveShips.add(ship)
        return Ok(Unit)
    }

    //    Метод проверяет что корабль не выходит за пределы игрового поля.
    private fun isInsideField(ship: Ship): Boolean = ship.doesFitInField(width, height)

    //    Метод проверяет что корабль не касается другого корабля.
    private fun canAddShip(ship: Ship): Boolean {
        for (existingShip in ships) {
            if (!existingShip.canPlaceNear(ship)) {
                return false
            }
        }
        return true
    }
    //Метод возвращает одно из трёх состояний корабля после выстрела: ранен, убит, выстрел прошел мимо.
    fun takeAShot(shot: Cell): ShotResult {
        for (ship in aliveShips) {
            val shotResult = ship.takeAShot(shot)
            if (shotResult == ShotResult.Kill) {
                aliveShips.remove(ship)
                return shotResult
            }
            else if (shotResult == ShotResult.Hit) {
                return shotResult
            }
        }
        return ShotResult.Miss
    }

    fun noMoreAliveShips() : Boolean = aliveShips.isEmpty()

    fun howManyShips(): Int {
        return ships.size
    }

    fun clearField(field: Field) {
        ships.clear()
        aliveShips.clear()
    }
}

