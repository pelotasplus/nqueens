package pl.pelotasplus.queens.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.pelotasplus.queens.ui.theme.NQueensTheme

@Composable
fun GameBoard(
    state: GameBoardState,
    modifier: Modifier = Modifier,
    onTileClicked: (Int, Int) -> Unit = { _, _ -> }
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val tileSize = this.maxWidth / state.size
        LazyVerticalGrid(
            columns = GridCells.Fixed(state.size),
            modifier = Modifier
                .padding(16.dp)
                .size(tileSize * state.size)
        ) {
            items(state.size * state.size) { index ->
                val row = index / state.size
                val col = index % state.size
                val gridState = state.grid[row][col]
                val isLight = (row + col) % 2 == 0

                Box(
                    modifier = Modifier
                        .size(tileSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false),
                            onClick = {
                                onTileClicked(row, col)
                            }
                        )
                        .background(if (isLight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer)
                ) {
                    when (gridState) {
                        is GameBoardPositionState.BlockedBy -> {
                            val sb = StringBuilder()
                            gridState.positions.forEach {
                                sb.append("(${it.row}x${it.col})\n")
                            }
                            Text(
                                sb.toString(),
                                color = MaterialTheme.colorScheme.surfaceContainer
                            )
                        }

                        GameBoardPositionState.Empty -> {
                            // nothing to show
                        }

                        GameBoardPositionState.Queen -> {
                            Image(
                                painter = painterResource(state.avatar),
                                modifier = Modifier.size(tileSize),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameBoardPreview() {
    NQueensTheme {
        Column {
            GameBoard(
                state = GameBoardState(
                    size = 4,
                )
            )
            GameBoard(
                state = GameBoardState(
                    size = 8,
                )
            )
        }
    }
}
