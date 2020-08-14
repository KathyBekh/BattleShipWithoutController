package com.git.kathyBeh.battleShips.model

import com.git.kathyBeh.battleShips.view.BattleShipView

class HumanPlayer : Player {

    override fun shoot(): Cell {
        return BattleShipView().askForShot()
    }
}