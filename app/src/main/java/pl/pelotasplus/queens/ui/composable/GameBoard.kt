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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.ui.theme.NQueensTheme

data class GameBoardPosition(
    val row: Int,
    val col: Int,
)

data class GameBoardState(
    val size: Int,
    val pieces: Set<GameBoardPosition> = emptySet(),
    val avatar: Int = R.drawable.avatar1
) {
    val movesLeft: Int
        get() = size - pieces.size
}

@Composable
fun GameBoard(
    state: GameBoardState,
    modifier: Modifier = Modifier,
    onTileClicked: (GameBoardPosition) -> Unit = { _ -> }
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
                val position = GameBoardPosition(row, col)
                val isLight = (row + col) % 2 == 0

                Box(
                    modifier = Modifier
                        .size(tileSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false),
                            onClick = {
                                onTileClicked(position)
                            }
                        )
                        .background(if (isLight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer)
                ) {
                    if (state.pieces.contains(position)) {
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameBoardPreview() {
    NQueensTheme {
        Column {
            GameBoard(
                state = GameBoardState(
                    size = 4,
                    pieces = setOf(
                        GameBoardPosition(0, 0),
                        GameBoardPosition(1, 1),
                        GameBoardPosition(2, 2),
                        GameBoardPosition(3, 3),
                    )
                )
            )
            GameBoard(
                state = GameBoardState(
                    size = 8,
                    pieces = setOf(
                        GameBoardPosition(0, 0),
                        GameBoardPosition(1, 1),
                        GameBoardPosition(2, 2),
                        GameBoardPosition(3, 3),
                    )
                )
            )
        }
    }
}
