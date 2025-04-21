package pl.pelotasplus.queens.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import pl.pelotasplus.queens.domain.GameBoardState
import pl.pelotasplus.queens.domain.PositionState
import pl.pelotasplus.queens.features.gamescreen.ShakingImage
import pl.pelotasplus.queens.ui.theme.NQueensTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameBoard(
    state: GameBoardState,
    modifier: Modifier = Modifier,
    onTileClick: (Int, Int) -> Unit = { _, _ -> },
    onAnimationFinish: (Int, Int) -> Unit = { _, _ -> }
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val tileSize = this.maxWidth / state.size
        val tileSizePx = with(density) { tileSize.toPx() }

        Box(
            modifier = Modifier
                .size(tileSize * state.size)
        ) {
            for (row in 0 until state.size) {
                for (col in 0 until state.size) {
                    val isLight = (row + col) % 2 == 0
                    Box(
                        modifier = Modifier
                            .semantics { testTagsAsResourceId = true }
                            .testTag("Grid${row}x${col}")
                            .size(tileSize)
                            .graphicsLayer(
                                translationX = col * tileSizePx,
                                translationY = row * tileSizePx
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = false),
                                onClick = {
                                    onTileClick(row, col)
                                }
                            )
                            .background(
                                if (isLight) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                }
                            )
                    )
                }
            }
            for (row in 0 until state.size) {
                for (col in 0 until state.size) {
                    val gridState = state.grid[row][col]
                    val translationX = col * tileSizePx
                    val translationY = row * tileSizePx

                    when (gridState) {
                        is PositionState.BlockedBy -> {
                            // uncomment to show blocked positions
                            /*
                            val sb = StringBuilder()
                            gridState.positions.forEach {
                                sb.append("(${it.row}x${it.col})\n")
                            }
                            Text(
                                sb.toString(),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                modifier = Modifier
                                    .size(tileSize)
                                    .graphicsLayer(
                                        translationX = translationX,
                                        translationY = translationY
                                    ),
                            )
                             */
                        }

                        is PositionState.Empty -> {
                            // nothing to show
                        }

                        is PositionState.Queen -> {
                            ShakingImage(
                                queen = gridState,
                                imageResId = state.avatar,
                                modifier = Modifier
                                    .size(tileSize)
                                    .graphicsLayer(
                                        translationX = translationX,
                                        translationY = translationY
                                    ),
                                onAnimationFinish = {
                                    onAnimationFinish(row, col)
                                }
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
