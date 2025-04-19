package pl.pelotasplus.queens.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pl.pelotasplus.queens.features.game_screen.ShakingImage
import pl.pelotasplus.queens.ui.theme.NQueensTheme

@Composable
fun GameBoard(
    state: GameBoardState,
    modifier: Modifier = Modifier,
    label: String = "",
    onTileClicked: (Int, Int) -> Unit = { _, _ -> },
    onAnimationFinished: (Int, Int) -> Unit = { _, _ -> }
) {
    println("XXX GameBoard new state ${state} -> $label")
    state.dump()

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val tileSize = this.maxWidth / state.size

        Column {
            for (row in 0 until state.size) {
                Row {
                    for (col in 0 until state.size) {
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
                                .background(
                                    if (isLight) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    }
                                )
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

                                is GameBoardPositionState.Empty -> {
                                    // nothing to show
                                }

                                is GameBoardPositionState.Queen -> {
                                    ShakingImage(
                                        queen = gridState,
                                        imageResId = state.avatar,
                                        modifier = Modifier
                                            .size(tileSize * 0.8f)
                                            .align(Alignment.Center),
                                        onAnimationFinished = {
                                            onAnimationFinished(row, col)
                                        }
                                    )
                                }
                            }
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
