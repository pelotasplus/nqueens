package pl.pelotasplus.queens.domain

import junit.framework.TestCase.assertEquals
import org.junit.Test
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.domain.PositionState.Queen

class GameBoardStateTest {

    @Test
    fun `getMovesLeft when grid is empty`() {
        val state = GameBoardState(size = 4)

        assertEquals(4, state.movesLeft)

        val state8 = GameBoardState(size = 8)

        assertEquals(8, state8.movesLeft)
    }

    @Test
    fun `getMovesLeft changes as queens are put on the board`() {
        GameBoardState(size = 4)
            .handleClick(1, 0)
            .also {
                assertEquals(3, it.movesLeft)
            }
            .handleClick(0, 2)
            .also {
                assertEquals(2, it.movesLeft)
            }
            .handleClick(3, 1)
            .also {
                assertEquals(1, it.movesLeft)
            }
            .handleClick(2, 3)
            .also {
                assertEquals(0, it.movesLeft)
            }
    }

    @Test
    fun `emptyGrid returns new grid`() {
        val state = GameBoardState(size = 4)

        val newState = state.handleClick(0, 0)

        assertEquals(Queen(false), newState.grid[0][0])

        val emptyState = newState.emptyGrid()

        assertEquals(newState.grid[0][0], Queen(false))
        assertEquals(emptyState.grid[0][0], PositionState.Empty)
    }

    @Test
    fun `emptyGrid size`() {
        val state = GameBoardState(size = 4)

        val emptyState = state.emptyGrid()

        assertEquals(4, emptyState.size)
    }

    @Test
    fun `getSize returns correct value`() {
        val state4 = GameBoardState(size = 4)

        assertEquals(4, state4.size)

        val state8 = GameBoardState(size = 8)

        assertEquals(8, state8.size)
    }

    @Test
    fun `getAvatar returns correct value`() {
        val state4 = GameBoardState(size = 4, avatar = R.drawable.avatar1)

        assertEquals(R.drawable.avatar1, state4.avatar)

        val state8 = GameBoardState(size = 8, avatar = R.drawable.avatar2)

        assertEquals(R.drawable.avatar2, state8.avatar)
    }

    @Test
    fun `toggle Queen on the same position`() {
        GameBoardState(size = 4)
            .handleClick(0, 0)
            .also {
                assertEquals(Queen(false), it.grid[0][0])
            }
            .handleClick(0, 0)
            .also {
                assertEquals(PositionState.Empty, it.grid[0][0])
            }
    }

    @Test
    fun `shake Queen when clicking blocked position`() {
        GameBoardState(size = 4)
            .handleClick(1, 0)
            .also {
                assertEquals(Queen(shake = false), it.grid[1][0])
            }
            .handleClick(1, 1)
            .also {
                assertEquals(Queen(shake = true), it.grid[1][0])
            }
            .handleClick(0, 2)
            .also {
                assertEquals(Queen(shake = false), it.grid[0][2])
            }
            .handleClick(3, 2)
            .also {
                assertEquals(Queen(shake = true), it.grid[0][2])
            }
    }

    @Test
    fun `mark blocked positions`() {
        val state = GameBoardState(size = 4).handleClick(1, 1)

        val blockedBy = PositionState.BlockedBy(listOf(GridPosition(1, 1)))

        assertEquals(
            listOf(
                blockedBy,
                blockedBy,
                blockedBy,
                PositionState.Empty,
            ),
            state.grid[0]
        )
        assertEquals(
            listOf(
                blockedBy,
                Queen(shake = false),
                blockedBy,
                blockedBy,
            ),
            state.grid[1]
        )
        assertEquals(
            listOf(
                blockedBy,
                blockedBy,
                blockedBy,
                PositionState.Empty,
            ),
            state.grid[2]
        )
        assertEquals(
            listOf(
                PositionState.Empty,
                blockedBy,
                PositionState.Empty,
                blockedBy,
            ),
            state.grid[3]
        )
    }

    @Test
    fun `mark double blocked positions`() {
        val state = GameBoardState(size = 4)
            .handleClick(1, 1)
            .handleClick(3, 2)

        val blockedByFirst = PositionState.BlockedBy(listOf(GridPosition(1, 1)))
        val blockedBySecond = PositionState.BlockedBy(listOf(GridPosition(3, 2)))
        val blockedByBoth = PositionState.BlockedBy(listOf(GridPosition(1, 1), GridPosition(3, 2)))

        assertEquals(
            listOf(
                blockedByFirst,
                blockedByFirst,
                blockedByBoth,
                PositionState.Empty,
            ),
            state.grid[0]
        )
        assertEquals(
            listOf(
                blockedByBoth,
                Queen(shake = false),
                blockedByBoth,
                blockedByFirst,
            ),
            state.grid[1]
        )
        assertEquals(
            listOf(
                blockedByFirst,
                blockedByBoth,
                blockedByBoth,
                blockedBySecond,
            ),
            state.grid[2]
        )
        assertEquals(
            listOf(
                blockedBySecond,
                blockedByBoth,
                Queen(shake = false),
                blockedByBoth,
            ),
            state.grid[3]
        )
    }
}
