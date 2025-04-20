package pl.pelotasplus.queens.domain

import pl.pelotasplus.queens.R
import kotlin.math.min

data class GameBoardState(
    val size: Int,
    val avatar: Int = R.drawable.avatar1,
    val grid: Grid = createEmptyGrid(size),
) {
    val movesLeft: Int
        get() = size - grid.flatten().count { it is PositionState.Queen }

    fun emptyGrid(): GameBoardState {
        return copy(
            grid = createEmptyGrid(size)
        )
    }

    fun handleClick(
        row: Int,
        col: Int
    ): GameBoardState {
        val positionState = grid[row][col]

        val newState = when (positionState) {
            is PositionState.BlockedBy -> {
                shakeQueen(positionState.positions, true)
            }

            is PositionState.Empty -> {
                toggleQueen(row, col).blockOthers(row, col, true)
            }

            is PositionState.Queen -> {
                toggleQueen(row, col).blockOthers(row, col, false)
            }
        }

        return newState
    }

    fun shakeQueen(
        positions: List<GridPosition>,
        shake: Boolean
    ): GameBoardState {
        val newGrid = grid.mutate()

        positions.forEach { position ->
            if (newGrid[position.row][position.col] is PositionState.Queen) {
                newGrid[position.row][position.col] = PositionState.Queen(shake)
            }
        }

        return copy(
            grid = newGrid
        )
    }

    private fun toggleQueen(
        row: Int,
        col: Int
    ): GameBoardState {
        val newGrid = grid.mutate()

        if (newGrid[row][col] is PositionState.Queen) {
            newGrid[row][col] = PositionState.Empty
        } else if (newGrid[row][col] is PositionState.Empty) {
            newGrid[row][col] = PositionState.Queen(false)
        }

        return copy(
            grid = newGrid
        )
    }

    private fun blockOthers(
        row: Int,
        col: Int,
        block: Boolean
    ): GameBoardState {
        val newGrid = grid.mutate()

        // visiting current row and col
        for (i in 0 until size) {
            if (block) {
                newGrid[row][i] += PositionState.BlockedBy(
                    listOf(GridPosition(row, col))
                )
                newGrid[i][col] += PositionState.BlockedBy(
                    listOf(GridPosition(row, col))
                )
            } else {
                newGrid[row][i] -= PositionState.BlockedBy(
                    listOf(GridPosition(row, col))
                )
                newGrid[i][col] -= PositionState.BlockedBy(
                    listOf(GridPosition(row, col))
                )
            }
        }

        //  visiting top-left diagonal
        val delta = min(row, col)
        var dr = row - delta
        var dc = col - delta

        while (dr < size && dc < size) {
            if (block) {
                newGrid[dr][dc] += PositionState.BlockedBy(
                    listOf(GridPosition(row, col))
                )
            } else {
                newGrid[dr][dc] -= PositionState.BlockedBy(
                    listOf(GridPosition(row, col))
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
                newGrid[dr2][dc2] += PositionState.BlockedBy(
                    listOf(GridPosition(row, col))
                )
            } else {
                newGrid[dr2][dc2] -= PositionState.BlockedBy(
                    listOf(GridPosition(row, col))
                )
            }
            dr2 += 1
            dc2 -= 1
        }

        return copy(
            grid = newGrid
        )
    }
}
