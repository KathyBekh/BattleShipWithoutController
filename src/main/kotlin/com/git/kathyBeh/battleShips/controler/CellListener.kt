package com.git.kathyBeh.battleShips.controler

import com.git.kathyBeh.battleShips.model.Cell


interface CellListener {
    fun cellClicked(cell: Cell)
}
