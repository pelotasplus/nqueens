package pl.pelotasplus.queens.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.pelotasplus.queens.ui.theme.NQueensTheme

data class GameBoardState(
    val size: Int,
)

@Composable
fun GameBoard(
    state: GameBoardState,
    modifier: Modifier = Modifier
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
                val isLight = (row + col) % 2 == 0

                Box(
                    modifier = Modifier
                        .size(tileSize)
                        .background(if (isLight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer)
                )
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
                    size = 4
                )
            )
            GameBoard(
                state = GameBoardState(
                    size = 8
                )
            )
        }
    }
}
