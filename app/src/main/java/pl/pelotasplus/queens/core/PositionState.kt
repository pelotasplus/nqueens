package pl.pelotasplus.queens.core

sealed interface PositionState {
    data object Empty : PositionState

    data class Queen(
        val shake: Boolean = false
    ) : PositionState

    data class BlockedBy(
        val positions: List<Position>
    ) : PositionState

    operator fun plus(other: PositionState): PositionState {
        if (this is Empty && other is Queen) {
            return other
        }
        if (this is BlockedBy && other is BlockedBy) {
            return BlockedBy(this.positions + other.positions)
        }
        if (this is Queen && other is Queen) {
            return Empty
        }
        if (this is Queen && other is BlockedBy) {
            return this
        }
        return other
    }

    operator fun minus(other: PositionState): PositionState {
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
