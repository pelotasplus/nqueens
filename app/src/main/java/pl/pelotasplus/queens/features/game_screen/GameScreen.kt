package pl.pelotasplus.queens.features.game_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.core.ObserveEffects
import pl.pelotasplus.queens.core.Position
import pl.pelotasplus.queens.core.SoundPlayer
import pl.pelotasplus.queens.core.formatTime
import pl.pelotasplus.queens.core.vibrate

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
                GameViewModel.Event.OnTileClicked(Position(row, col))
            )
        },
        onAnimationFinished = { row, col ->
            viewModel.handleEvent(
                GameViewModel.Event.OnAnimationFinished(Position(row, col))
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
