package pl.pelotasplus.queens.domain

import junit.framework.TestCase.assertEquals
import org.junit.Test

class GridTest {

    @Test
    fun `createEmptyGrid with positive size`() {
        val grid = createEmptyGrid(5)

        for (row in grid) {
            for (position in row) {
                assertEquals(PositionState.Empty, position)
            }
        }
    }

    @Test
    fun `mutate with empty grid`() {
        val grid = createEmptyGrid(5)

        val mutatedGrid = grid.mutate()

        mutatedGrid[2][2] = PositionState.Queen(false)

        assertEquals(PositionState.Queen(false), mutatedGrid[2][2])
    }
}
