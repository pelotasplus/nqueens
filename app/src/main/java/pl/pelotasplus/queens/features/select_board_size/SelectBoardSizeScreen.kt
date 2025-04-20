package pl.pelotasplus.queens.features.select_board_size

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.core.ObserveEffects
import pl.pelotasplus.queens.domain.model.Avatar
import pl.pelotasplus.queens.ui.theme.NQueensTheme

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
        onBoardSizeSelected = {
            viewModel.handleEvent(SelectBoardSizeViewModel.Event.OnBoardSizeSelected(it))
        }
    )
}
