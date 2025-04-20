package pl.pelotasplus.queens.features.game_screen

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.core.ObserveEffects
import pl.pelotasplus.queens.ui.composable.GameBoard
import pl.pelotasplus.queens.ui.composable.GameBoardPosition
import pl.pelotasplus.queens.ui.composable.GameBoardState
import pl.pelotasplus.queens.ui.theme.NQueensTheme

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val player = remember { SoundPlayer(context) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showFinishedDialog by remember {
        mutableStateOf<GameViewModel.Effect.ShowFinished.WinnerDetails?>(
            null
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    ObserveEffects(viewModel.effect) { effect ->
        when (effect) {
            is GameViewModel.Effect.ShowFinished -> {
                showFinishedDialog = effect.winnerDetails
            }

            GameViewModel.Effect.Vibrate -> {
                player.playSound(R.raw.animals_dog_barking_small)
                vibrate(context)
            }
        }
    }

    GameContent(
        modifier = modifier,
        state = state,
        onRetryClicked = {
            viewModel.handleEvent(GameViewModel.Event.OnPlayAgainClicked)
        },
        onTrophyClicked = {

        },
        onTileClicked = { row, col ->
            viewModel.handleEvent(
                GameViewModel.Event.OnTileClicked(
                    GameBoardPosition(row, col)
                )
            )
        },
        onAnimationFinished = { row, col ->
            viewModel.handleEvent(
                GameViewModel.Event.OnAnimationFinished(
                    GameBoardPosition(row, col)
                )
            )
        }
    )

    showFinishedDialog?.let {
        FinishedDialog(
            winnerName = it.avatar.name,
            timeElapsed = formatTime(it.timeElapsed),
            onPlayAgain = {
                showFinishedDialog = null
                viewModel.handleEvent(GameViewModel.Event.OnPlayAgainClicked)
            },
            onDismiss = {
                showFinishedDialog = null
            }
        )
    }
}

private fun vibrate(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = vibratorManager.defaultVibrator
        val vibrationEffect =
            VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect =
            VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }
}

@Composable
private fun GameContent(
    state: GameViewModel.State,
    modifier: Modifier = Modifier,
    onTrophyClicked: () -> Unit = {},
    onRetryClicked: () -> Unit = {},
    onTileClicked: (Int, Int) -> Unit = { _, _ -> },
    onAnimationFinished: (Int, Int) -> Unit = { _, _ -> }
) {
    Column(modifier.padding(8.dp)) {
        Row {
            Image(
                painter = painterResource(R.drawable.trophy),
                modifier = Modifier
                    .size(80.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false),
                        onClick = {
                            onTrophyClicked()
                        }
                    ),
                contentDescription = null
            )

            Spacer(Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.retry),
                modifier = Modifier
                    .size(80.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false),
                        onClick = {
                            onRetryClicked()
                        }
                    ),
                contentDescription = null
            )

        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            GameBoard(
                modifier = Modifier.align(Alignment.Center),
                state = state.boardState,
                onTileClicked = onTileClicked,
                onAnimationFinished = onAnimationFinished
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
                    style = MaterialTheme.typography.displayLarge
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
