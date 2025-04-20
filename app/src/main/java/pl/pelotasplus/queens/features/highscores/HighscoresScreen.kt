package pl.pelotasplus.queens.features.highscores

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.pelotasplus.queens.core.ObserveEffects

@Composable
fun HighscoresScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HighscoresViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            HighscoresViewModel.Effect.NavigateUp -> {
                navigateUp()
            }
        }
    }

    HighscoresContent(
        modifier = modifier,
        state = state,
        onNavigateUpClick = {
            viewModel.handleEvent(HighscoresViewModel.Event.NavigateUp)
        }
    )
}

