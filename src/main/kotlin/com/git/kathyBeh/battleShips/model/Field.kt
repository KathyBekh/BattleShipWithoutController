package com.git.kathyBeh.battleShips.model


import java.util.*


/**
 * Разработать программу с графическим интерфейсом, реализующую указанную в
задании функциональность, на языках Java и/или Kotlin. При проектировании
использовать объектно-ориентированный подход. Разработать автоматические тесты
для отдельных частей программы. Написанный код держать в отдельном проекте или
модуле в репозитории на GitHub.
 */

class Field @JvmOverloads constructor(val width: Int = 10, val height: Int = 10) {
    private val ships: MutableMap<Cell, Ship> = HashMap()
    var turn: Ship = Ship.BLUE
    private var listener: BoardListener? = null

    fun clear() {
        ships.clear()
        turn = Ship.BLUE
    }

    fun registerListener(listener: BoardListener) {
        this.listener = listener
    }

    operator fun get(x: Int, y: Int): Ship? {
        return get(Cell(x, y))
    }

    operator fun get(cell: Cell): Ship? {
        return ships[cell]
    }

//    fun getTurn(): Ship {
//        return turn
//    }

    fun makeTurn(x: Int): Cell? {
        return makeTurn(x, true)
    }

    fun makeTurnNoEvent(x: Int): Cell? {
        return makeTurn(x, false)
    }

    private fun makeTurn(x: Int, withEvent: Boolean): Cell? {
        if (x < 0 || x >= width) return null
        for (y in 0 until height) {
            val cell = Cell(x, y)
            if (!ships.containsKey(cell)) {
                ships[cell] = turn
                turn = turn.opposite()
                if (listener != null && withEvent) {
                    listener!!.turnMade(cell)
                }
                return cell
            }
        }
        return null
    }

    fun hasFreeCells(): Boolean {
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (get(x, y) == null) return true
            }
        }
        return false
    }

    fun correct(cell: Cell): Boolean {
        return cell.x in 0 until width && cell.y >= 0 && cell.y < height
    }

    fun winner(): Ship? {
        val combo: WinningCombo = winningCombo() ?: return null
        return combo.winner
    }

    fun winningCombo(): WinningCombo? {
        for (x in 0 until width) {
            for (y in 0 until height) {
                val cell = Cell(x, y)
                val startChip: Ship = ships[cell] ?: continue
                // Vector-style
                for (dir in DIRECTIONS) {
                    var current = cell
                    var length = 1
                    while (length < TO_WIN_LENGTH) {
                        current = current.plus(dir)
                        if (get(current) !== startChip) break
                        length++
                    }
                    if (length == TO_WIN_LENGTH) return WinningCombo(startChip, cell, current, dir)
                }
            }
        }
        return null

    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in height - 1 downTo 0) {
            for (x in 0 until width) {
                val ship: Ship? = get(x, y)
                if (ship == null) {
                    sb.append("- ")
                    continue
                }
                when (ship) {
                    Ship.BLUE -> sb.append("B ")
                    Ship.RED -> sb.append("r ")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    fun takeTurnBack(x: Int) {
        for (y in height - 1 downTo 0) {
            val cell = Cell(x, y)
            val ship: Ship? = get(cell)
            if (ship != null) {
                ships.remove(cell)
                turn = turn.opposite()
                return
            }
        }
    }

    companion object {
        const val TO_WIN_LENGTH = 4
        private val DIRECTIONS = arrayOf(
            Cell(0, 1), Cell(1, 0),
            Cell(1, 1), Cell(1, -1)
        )
    }

}

