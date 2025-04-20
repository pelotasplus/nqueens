package pl.pelotasplus.queens.features.selectboardsize

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.pelotasplus.queens.core.ObserveEffects
import pl.pelotasplus.queens.domain.Avatar

@Composable
fun SelectBoardSizeScreen(
    modifier: Modifier = Modifier,
    viewModel: SelectBoardSizeViewModel = hiltViewModel(),
    startGame: (Avatar, Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffects(viewModel.effect) { effect ->
        when (effect) {
            is SelectBoardSizeViewModel.Effect.StartGame -> {
                startGame(effect.avatar, effect.size)
            }
        }
    }

    SelectBoardSizeContent(
        modifier = modifier,
        state = state,
        onBoardSizeSelect = {
            viewModel.handleEvent(SelectBoardSizeViewModel.Event.OnBoardSizeSelected(it))
        }
    )
}
