package com.git.kathyBeh.battleShips.model

data class ShipPlacementDetails(
    val length: Int,
    val initialCell: Cell,
    val direction: Direction
)