package com.git.kathyBeh.battleShips.controler

import com.git.kathyBeh.battleShips.model.Cell
import com.git.kathyBeh.battleShips.model.Field


class BoardBasedCellListener(board: Field) : CellListener {
    private val board: Field = board
    override fun cellClicked(cell: Cell) {
        if (board.winner() == null) {
            board.makeTurn(cell.x)
        }
    }

}