package pl.pelotasplus.queens.features.gamescreen

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
import pl.pelotasplus.queens.core.SoundPlayer
import pl.pelotasplus.queens.core.formatTime
import pl.pelotasplus.queens.core.vibrate
import pl.pelotasplus.queens.domain.GridPosition

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    goToHighscores: () -> Unit
) {
    val context = LocalContext.current
    val player = remember { SoundPlayer(context) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showFinishedDialog by remember {
        mutableStateOf<GameViewModel.Effect.ShowFinished?>(
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
                showFinishedDialog = effect
            }

            GameViewModel.Effect.Vibrate -> {
                player.playSound(R.raw.animals_dog_barking_small)
                vibrate(context)
            }

            GameViewModel.Effect.ShowHighscores -> {
                goToHighscores()
            }
        }
    }

    GameContent(
        modifier = modifier,
        state = state,
        onRetryClick = {
            viewModel.handleEvent(GameViewModel.Event.OnPlayAgainClicked)
        },
        onTrophyClick = {
            viewModel.handleEvent(GameViewModel.Event.OnTrophyClicked)
        },
        onTileClick = { row, col ->
            viewModel.handleEvent(
                GameViewModel.Event.OnTileClicked(GridPosition(row, col))
            )
        },
        onAnimationFinish = { row, col ->
            viewModel.handleEvent(
                GameViewModel.Event.OnAnimationFinished(GridPosition(row, col))
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
