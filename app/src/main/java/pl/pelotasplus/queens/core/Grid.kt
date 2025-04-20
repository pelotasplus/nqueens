package pl.pelotasplus.queens.core

import pl.pelotasplus.queens.core.PositionState.Empty

typealias Grid = List<List<PositionState>>

data class Position(val row: Int, val col: Int)

fun createEmptyGrid(size: Int) =
    List(size) { row -> List(size) { col -> Empty } }

fun Grid.mutate() =
    this.map { row -> row.toMutableList() }.toMutableList()
