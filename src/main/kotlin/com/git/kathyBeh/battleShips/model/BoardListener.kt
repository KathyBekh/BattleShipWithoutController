package com.git.kathyBeh.battleShips.model


interface BoardListener {
    fun turnMade(cell: Cell)
}