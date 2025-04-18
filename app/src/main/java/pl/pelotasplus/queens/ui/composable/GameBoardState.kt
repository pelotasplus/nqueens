package pl.pelotasplus.queens.ui.composable

import pl.pelotasplus.queens.R

data class GameBoardState(
    val size: Int,
    val avatar: Int = R.drawable.avatar1,
    val grid: Array<Array<GameBoardPositionState>> = Array(size) {
        Array(size) { GameBoardPositionState.Empty }
    }
) {
    val movesLeft: Int
        get() = size - grid.flatten().count { it is GameBoardPositionState.Queen }
}

sealed interface GameBoardPositionState {
    object Empty : GameBoardPositionState
    object Queen : GameBoardPositionState
    data class BlockedBy(val positions: List<GameBoardPosition>) : GameBoardPositionState

    operator fun plus(other: GameBoardPositionState): GameBoardPositionState {
        if (this == Empty && other == Queen) {
            return Queen
        }
        if (this is BlockedBy && other is BlockedBy) {
            return BlockedBy(this.positions + other.positions)
        }
        if (this is Queen && other is Queen) {
            return Empty
        }
        if (this is Queen && other is BlockedBy) {
            return Queen
        }
        return other
    }

    operator fun minus(other: GameBoardPositionState): GameBoardPositionState {
        if (this is BlockedBy && other is BlockedBy) {
            val newPositions = this.positions - other.positions
            if (newPositions.isEmpty()) {
                return Empty
            } else {
                return BlockedBy(newPositions)
            }
        }
        return this
    }
}

data class GameBoardPosition(
    val row: Int,
    val col: Int
)
