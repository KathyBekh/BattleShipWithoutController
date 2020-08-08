package com.git.kathyBeh.battleShips.model

enum class Ship {
    BLUE, RED;

    fun opposite(): Ship {
        return if (this == BLUE) RED else BLUE
    }
}