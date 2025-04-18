package pl.pelotasplus.queens.ui.composable

import pl.pelotasplus.queens.R

data class GameBoardState(
    val size: Int,
    val avatar: Int = R.drawable.avatar1,
    val grid: Array<Array<GameBoardPositionState>> = Array(size) { row ->
        Array(size) { col ->
            GameBoardPositionState.Empty(row, col)
        }
    },
) {
    fun dump() {
        grid.forEachIndexed { i, row ->
            row.forEachIndexed { j, state ->
                println("$i x $j -> $state")
            }
        }
    }

    val movesLeft: Int
        get() = size - grid.flatten().count { it is GameBoardPositionState.Queen }
}

sealed interface GameBoardPositionState {
    data class Empty(
        val row: Int,
        val col: Int
    ) : GameBoardPositionState

    data class Queen(
        val row: Int,
        val col: Int,
        val shake: Boolean = false
    ) : GameBoardPositionState

    data class BlockedBy(
        val row: Int,
        val col: Int,
        val positions: List<GameBoardPosition>
    ) : GameBoardPositionState

    operator fun plus(other: GameBoardPositionState): GameBoardPositionState {
        if (this is Empty && other is Queen) {
            return other
        }
        if (this is BlockedBy && other is BlockedBy) {
            return BlockedBy(this.row, this.col, this.positions + other.positions)
        }
        if (this is Queen && other is Queen) {
            return Empty(this.row, this.col)
        }
        if (this is Queen && other is BlockedBy) {
            return this
        }
        return other
    }

    operator fun minus(other: GameBoardPositionState): GameBoardPositionState {
        if (this is BlockedBy && other is BlockedBy) {
            val newPositions = this.positions - other.positions
            if (newPositions.isEmpty()) {
                return Empty(this.row, this.col)
            } else {
                return BlockedBy(this.row, this.col, newPositions)
            }
        }
        return this
    }
}

data class GameBoardPosition(
    val row: Int,
    val col: Int
)
