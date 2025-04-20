package pl.pelotasplus.queens.domain

import androidx.compose.ui.graphics.vector.EmptyPath
import junit.framework.TestCase.assertEquals
import org.junit.Test
import pl.pelotasplus.queens.domain.PositionState.BlockedBy
import pl.pelotasplus.queens.domain.PositionState.Empty
import pl.pelotasplus.queens.domain.PositionState.Queen

class PositionStateTest {

    @Test
    fun `plus Empty and Queen`() {
        assertEquals(Queen(false), Empty + Queen(false))
    }

    @Test
    fun `plus BlockedBy and BlockedBy`() {
        assertEquals(
            BlockedBy(listOf(GridPosition(0, 0), GridPosition(1, 1))),
            BlockedBy(listOf(GridPosition(0, 0))) + BlockedBy(listOf(GridPosition(1, 1)))
        )
    }

    @Test
    fun `plus Queen and Queen`() {
        assertEquals(Empty, Queen(false) + Queen(false))
    }

    @Test
    fun `plus Queen and BlockedBy`() {
        assertEquals(
            Queen(false),
            Queen(false) + BlockedBy(listOf(GridPosition(0, 0)))
        )
    }

    @Test
    fun `plus Empty and Empty`() {
        assertEquals(Empty, Empty + Empty)
    }

    @Test
    fun `plus BlockedBy and Empty`() {
        assertEquals(
            BlockedBy(listOf(GridPosition(0, 0))),
            BlockedBy(listOf(GridPosition(0, 0))) + Empty
        )
    }

    @Test
    fun `plus BlockedBy with empty list`() {
        assertEquals(
            BlockedBy(listOf(GridPosition(0, 0), GridPosition(1, 1))),
            BlockedBy(listOf(GridPosition(0, 0), GridPosition(1, 1))) + BlockedBy(emptyList())
        )
    }

    @Test
    fun `plus Queen and Empty`() {
        assertEquals(Queen(false), Queen(false) + Empty)
    }

    @Test
    fun `plus BlockedBy and Queen`() {
        assertEquals(Queen(false), BlockedBy(listOf(GridPosition(0, 0))) + Queen(false))
    }

    @Test
    fun `minus BlockedBy and BlockedBy with shared position`() {
        assertEquals(
            BlockedBy(listOf(GridPosition(0, 0))),
            BlockedBy(listOf(GridPosition(0, 0), GridPosition(1, 1)))
                    - BlockedBy(listOf(GridPosition(1, 1)))
        )
    }

    @Test
    fun `minus BlockedBy and BlockedBy no shared position`() {
        assertEquals(
            BlockedBy(listOf(GridPosition(0, 0))),
            BlockedBy(listOf(GridPosition(0, 0)))
                    - BlockedBy(listOf(GridPosition(1, 1)))
        )
    }

    @Test
    fun `minus BlockedBy and BlockedBy all shared`() {
        assertEquals(
            Empty,
            BlockedBy(listOf(GridPosition(0, 0)))
                    - BlockedBy(listOf(GridPosition(0, 0)))
        )
    }

    @Test
    fun `minus BlockedBy and BlockedBy empty`() {
        assertEquals(
            BlockedBy(listOf(GridPosition(0, 0), GridPosition(1, 1))),
            BlockedBy(listOf(GridPosition(0, 0), GridPosition(1, 1)))
                    - BlockedBy(emptyList())
        )
    }

    @Test
    fun `minus Empty and BlockedBy`() {
        assertEquals(
            Empty,
            Empty - BlockedBy(listOf(GridPosition(0, 0)))
        )
    }

    @Test
    fun `minus Queen and BlockedBy`() {
        assertEquals(
            Queen(true),
            Queen(true) - BlockedBy(listOf(GridPosition(0, 0)))
        )
    }

    @Test
    fun `minus Empty and Queen`() {
        assertEquals(
            Empty,
            Empty - Queen(true)
        )
    }

    @Test
    fun `minus Queen and Empty`() {
        assertEquals(
            Queen(true),
            Queen(true) - Empty
        )
    }

    @Test
    fun `minus Empty and Empty`() {
        assertEquals(
            Empty,
            Empty - Empty
        )
    }

    @Test
    fun `minus BlockedBy and Empty`() {
        assertEquals(
            BlockedBy(listOf(GridPosition(0, 0))),
            BlockedBy(listOf(GridPosition(0, 0))) - Empty
        )
    }

    @Test
    fun `minus BlockedBy empty list and BlockedBy empty list`() {
        assertEquals(
            Empty,
            BlockedBy(emptyList()) - BlockedBy(emptyList())
        )
    }
}
