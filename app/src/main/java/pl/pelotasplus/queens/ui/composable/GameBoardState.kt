package pl.pelotasplus.queens.ui.composable

import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.ui.composable.GameBoardPositionState.BlockedBy
import pl.pelotasplus.queens.ui.composable.GameBoardPositionState.Empty
import pl.pelotasplus.queens.ui.composable.GameBoardPositionState.Queen
import kotlin.math.min

typealias Grid = List<List<GameBoardPositionState>>

private fun createEmptyGrid(size: Int) = List(size) { row ->
    List(size) { col ->
        Empty(row, col)
    }
}

private fun Grid.mutate() =
    this.map { row ->
        row.toMutableList()
    }.toMutableList()

data class GameBoardState(
    val size: Int,
    val avatar: Int = R.drawable.avatar1,
    val grid: Grid = createEmptyGrid(size),
) {
    fun emptyGrid(): GameBoardState {
        return copy(
            grid = createEmptyGrid(size)
        )
    }

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
    ): GameBoardState {
        val positionState = grid[row][col]
        println("XXX handleClick $row $col -> $positionState")

        val newState = when (positionState) {
            is BlockedBy -> {
                shakeQueen(positionState.positions, true)
            }

            is Empty -> {
                toggleQueen(row, col).blockOthers(row, col, true)
            }

            is Queen -> {
                toggleQueen(row, col).blockOthers(row, col, false)
            }
        }

        return newState
    }

    fun shakeQueen(
        positions: List<GameBoardPosition>,
        shake: Boolean
    ): GameBoardState {
        println("XXX shakeQueen $positions")

        val newGrid = grid.mutate()

        positions.forEach {
            val row = it.row
            val col = it.col
            if (newGrid[row][col] is Queen) {
                newGrid[row][col] = Queen(row, col, shake)
            }
        }

        return copy(
            grid = newGrid
        )
    }

    fun toggleQueen(
        row: Int,
        col: Int
    ): GameBoardState {
        println("XXX toggleQueen $row $col -> ${grid[row][col]}")

        val newGrid = grid.mutate()

        if (newGrid[row][col] is Queen) {
            newGrid[row][col] = Empty(row, col)
        } else if (newGrid[row][col] is Empty) {
            newGrid[row][col] = Queen(row, col, false)
        }

        return copy(
            grid = newGrid
        )
    }

    fun blockOthers(
        row: Int,
        col: Int,
        block: Boolean
    ): GameBoardState {
        println("XXX blockOthers $row $col $block")

        val newGrid = grid.mutate()

        // visiting current row and col
        for (i in 0 until size) {
            if (block) {
                newGrid[row][i] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
                newGrid[i][col] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            } else {
                newGrid[row][i] -= BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
                newGrid[i][col] -= BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            }
        }

        //  visiting top-left diagonal
        val delta = min(row, col)
        var dr = row - delta
        var dc = col - delta

        while (dr < size && dc < size) {
            if (block) {
                newGrid[dr][dc] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            } else {
                newGrid[dr][dc] -= BlockedBy(
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
                newGrid[dr2][dc2] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            } else {
                newGrid[dr2][dc2] -= BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            }
            dr2 += 1
            dc2 -= 1
        }

        return copy(
            grid = newGrid
        )
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
