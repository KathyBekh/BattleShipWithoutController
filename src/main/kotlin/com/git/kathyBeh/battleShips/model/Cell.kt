package com.git.kathyBeh.battleShips.model

import kotlin.math.abs
import kotlin.math.max

data class Cell(var x: Int, var y: Int) {
    //    Расстояние между двумя клетками должно быть не меньше 1, для выполнения правила "корабли не должны соприкасаться друг с другом".
    fun canPlaceNear(cell: Cell): Boolean = max(abs(cell.x - x), abs(cell.y - y)) > 1

    override fun toString(): String {
        return "($x, $y)"
    }

    fun haloCell (): Set<Cell> {
        val perimeter = mutableSetOf<Cell>()
        perimeter.add(Cell(x + 1, y + 1))
        perimeter.add(Cell(x - 1, y - 1))
        perimeter.add(Cell(x - 1, y + 1))
        perimeter.add(Cell(x + 1, y - 1))
        perimeter.add(Cell(x + 1, y))
        perimeter.add(Cell(x - 1, y))
        perimeter.add(Cell(x, y - 1))
        perimeter.add(Cell(x, y + 1))
        return perimeter
    }
}
