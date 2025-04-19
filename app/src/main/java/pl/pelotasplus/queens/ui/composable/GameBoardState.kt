package pl.pelotasplus.queens.ui.composable

import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.ui.composable.GameBoardPositionState.BlockedBy
import pl.pelotasplus.queens.ui.composable.GameBoardPositionState.Empty
import pl.pelotasplus.queens.ui.composable.GameBoardPositionState.Queen
import kotlin.math.min

data class GameBoardState(
    val size: Int,
    val avatar: Int = R.drawable.avatar1,
    val grid: Array<Array<GameBoardPositionState>> = Array(size) { row ->
        Array(size) { col ->
            Empty(row, col)
        }
    },
    val generationTime: Long = 0L
) {
    fun dump() {
        grid.forEachIndexed { i, row ->
            row.forEachIndexed { j, state ->
                println("XXX $i x $j -> $state")
            }
        }
    }

    fun handleClick(
        row: Int,
        col: Int
    ) {
        val positionState = grid[row][col]
        println("XXX handleClick $row $col -> $positionState")

        when (positionState) {
            is BlockedBy -> {
                positionState.positions.forEach {
                    shakeQueen(it.row, it.col, true)
                }
            }

            is Empty -> {
                toggleQueen(row, col)
                blockOthers(row, col, true)
            }

            is Queen -> {
                toggleQueen(row, col)
                blockOthers(row, col, false)
            }
        }
    }

    fun shakeQueen(
        row: Int,
        col: Int,
        shake: Boolean
    ) {
        println("XXX shakeQueen $row $col $shake -> ${grid[row][col]}")

        if (grid[row][col] is Queen) {
            grid[row][col] = Queen(row, col, shake)
        }
    }

    fun toggleQueen(
        row: Int,
        col: Int
    ) {
        println("XXX toggleQueen $row $col -> ${grid[row][col]}")

        if (grid[row][col] is Queen) {
            grid[row][col] = Empty(row, col)
        } else if (grid[row][col] is Empty) {
            grid[row][col] = Queen(row, col, false)
        }
    }

    fun blockOthers(
        row: Int,
        col: Int,
        block: Boolean
    ) {
        println("XXX blockOthers $row $col $block")

        // visiting current row and col
        for (i in 0 until size) {
            if (block) {
                grid[row][i] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
                grid[i][col] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            } else {
                grid[row][i] -= BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
                grid[i][col] -= BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            }
        }

        // visiting top-left diagonal
        val delta = min(row, col)
        var dr = row - delta
        var dc = col - delta

        while (dr < size && dc < size) {
            if (block) {
                grid[dr][dc] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            } else {
                grid[dr][dc] -= BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            }
            dr += 1
            dc += 1
        }

        // visiting top-right diagonal
        val delta2 = min(row, size - col - 1)
        var dr2 = row - delta2
        var dc2 = col + delta2
        while (dr2 < size && dc2 >= 0) {
            if (block) {
                grid[dr2][dc2] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            } else {
                grid[dr2][dc2] -= BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            }
            dr2 += 1
            dc2 -= 1
        }
    }

    val movesLeft: Int
        get() = size - grid.flatten().count { it is Queen }
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
