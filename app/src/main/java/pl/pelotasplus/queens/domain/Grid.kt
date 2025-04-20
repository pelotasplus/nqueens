package pl.pelotasplus.queens.domain

import pl.pelotasplus.queens.domain.PositionState.Empty

typealias Grid = List<List<PositionState>>

fun createEmptyGrid(size: Int) =
    List(size) { row -> List(size) { col -> Empty } }

fun Grid.mutate() =
    this.map { row -> row.toMutableList() }.toMutableList()
