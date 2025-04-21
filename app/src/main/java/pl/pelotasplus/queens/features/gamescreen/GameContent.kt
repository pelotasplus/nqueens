package pl.pelotasplus.queens.features.gamescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.domain.GameBoardState
import pl.pelotasplus.queens.ui.composable.GameBoard
import pl.pelotasplus.queens.ui.theme.NQueensTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun GameContent(
    state: GameViewModel.State,
    modifier: Modifier = Modifier,
    onTrophyClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onTileClick: (Int, Int) -> Unit = { _, _ -> },
    onAnimationFinish: (Int, Int) -> Unit = { _, _ -> }
) {
    Scaffold(modifier) {
        Column(
            Modifier
                .padding(it)
                .padding(8.dp)
        ) {
            Row {
                Image(
                    painter = painterResource(R.drawable.trophy),
                    modifier = Modifier
                        .semantics { testTagsAsResourceId = true }
                        .testTag("Highscores")
                        .size(80.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false),
                            onClick = {
                                onTrophyClick()
                            }
                        ),
                    contentDescription = null
                )

                Spacer(Modifier.weight(1f))

                RestartButton(
                    onRetryClick = onRetryClick,
                    modifier = Modifier
                        .semantics { testTagsAsResourceId = true }
                        .testTag("Restart")
                )
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                GameBoard(
                    modifier = Modifier.align(Alignment.Center),
                    state = state.boardState,
                    onTileClick = onTileClick,
                    onAnimationFinish = onAnimationFinish
                )
            }

            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(
                            id = state.selectedAvatar?.image ?: R.drawable.avatar1
                        ),
                        modifier = Modifier.size(80.dp),
                        contentDescription = null
                    )

                    Text(
                        text = state.boardState.movesLeft.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier
                            .semantics { testTagsAsResourceId = true }
                            .testTag("MovesLeft")
                    )
                }

                Spacer(Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    InfiniteTimer(
                        state.gameStatus == GameViewModel.State.GameStatus.InProgress,
                        state.gameStartTime
                    )

                    Image(
                        painter = painterResource(R.drawable.clock),
                        modifier = Modifier.size(80.dp),
                        contentDescription = null
                    )

                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameContentPreview() {
    NQueensTheme {
        GameContent(
            state = GameViewModel.State(
                boardState = GameBoardState(size = 8),
            )
        )
    }
}
