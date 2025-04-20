package pl.pelotasplus.queens.domain

sealed interface PositionState {
    data object Empty : PositionState

    data class Queen(
        val shake: Boolean = false
    ) : PositionState

    data class BlockedBy(
        val positions: List<GridPosition>
    ) : PositionState

    operator fun plus(other: PositionState): PositionState {
        return if (this is Empty && other is Queen) {
            other
        } else if (this is BlockedBy && other is BlockedBy) {
            BlockedBy(this.positions + other.positions)
        } else if (this is Queen && other is Queen) {
            Empty
        } else if (this is Queen && other is BlockedBy) {
            this
        } else if (other is Empty) {
            this
        } else {
            other
        }
    }

    operator fun minus(other: PositionState): PositionState {
        if (this is BlockedBy && other is BlockedBy) {
            val newPositions = this.positions - other.positions
            return if (newPositions.isEmpty()) {
                Empty
            } else {
                BlockedBy(newPositions)
            }
        }
        return this
    }
}
