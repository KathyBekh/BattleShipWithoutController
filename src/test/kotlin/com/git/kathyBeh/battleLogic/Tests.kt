package com.git.kathyBeh.battleLogic

import com.git.kathyBeh.battleShips.controler.GameController
import com.git.kathyBeh.battleShips.error.ShipPlacementError
import com.git.kathyBeh.battleShips.model.*
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class AddShipTests {

    private val contr = GameController()

    private val field = contr.playerField

    @Test
    fun successAddShip() {
        val ship = contr.createShip(ShipPlacementDetails(4, Cell(0, 0), Direction.Right))
        field.addShip(ship)
        val anotherShip = contr.createShip(ShipPlacementDetails(3, Cell(5, 2), Direction.Down))
        Assertions.assertEquals(field.addShip(anotherShip), Ok(Unit))
    }

    @Test
    fun shipOutsideTheField() {
        val ship = contr.createShip(ShipPlacementDetails(4, Cell(9, 9), Direction.Right))
        Assertions.assertEquals(field.addShip(ship), Err(ShipPlacementError.OutsideOfField))
    }

    @Test
    fun shipsTouch() {
        val ship = contr.createShip(ShipPlacementDetails(2, Cell(2, 2), Direction.Right))
        val anotherShip = contr.createShip(ShipPlacementDetails(3, Cell(1, 1), Direction.Down))
        field.addShip(ship)
        Assertions.assertEquals(field.addShip(anotherShip), Err(ShipPlacementError.NearAnotherShip))
    }

    @Test
    fun shipOnTheShip() {
        val ship = contr.createShip(ShipPlacementDetails(4, Cell(0, 0), Direction.Right))
        field.addShip(ship)
        val anotherShip = contr.createShip(ShipPlacementDetails(4, Cell(0, 0), Direction.Right))
        Assertions.assertEquals(field.addShip(anotherShip), Err(ShipPlacementError.NearAnotherShip))
    }

    @Test
    fun killShip() {
        val ship = contr.createShip(ShipPlacementDetails(1, Cell(2, 8), Direction.Right))
        field.addShip(ship)
        Assertions.assertEquals(field.takeAShot(Cell(2, 8)), ShotResult.Kill)
    }

    @Test
    fun missShip() {
        val ship = contr.createShip(ShipPlacementDetails(1, Cell(6, 7), Direction.Right))
        field.addShip(ship)
        Assertions.assertEquals(field.takeAShot(Cell(5, 9)), ShotResult.Miss)
    }

    @Test
    fun hitShip() {
        val ship = contr.createShip(ShipPlacementDetails(4, Cell(0, 0), Direction.Right))
        field.addShip(ship)
        Assertions.assertEquals(field.takeAShot(Cell(0, 0)), ShotResult.Hit)
    }

}