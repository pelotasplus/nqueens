package pl.pelotasplus.queens.features.pick_avatar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.pelotasplus.queens.core.ObserveEffects
import pl.pelotasplus.queens.domain.Avatar

@Composable
fun PickAvatarScreen(
    modifier: Modifier = Modifier,
    viewModel: PickAvatarViewModel = hiltViewModel(),
    goToSelectBoardSize: (Avatar) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffects(viewModel.effect) { effect ->
        when (effect) {
            is PickAvatarViewModel.Effect.GoToSelectBoardSize -> {
                goToSelectBoardSize(effect.avatar)
            }
        }

    }

    PickAvatarContent(
        modifier = modifier,
        state = state,
        onAvatarSelected = {
            viewModel.handleEvent(PickAvatarViewModel.Event.AvatarSelected(it))
        }
    )
}
