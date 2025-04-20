package pl.pelotasplus.queens.features.highscores

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.pelotasplus.queens.core.ObserveEffects

@Composable
fun HighscoresScreen(
    modifier: Modifier = Modifier,
    viewModel: HighscoresViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            HighscoresViewModel.Effect.NavigateUp -> {
                onNavigateUp()
            }
        }
    }

    HighscoresContent(
        modifier = modifier,
        state = state,
        onNavigateUpClicked = {
            viewModel.handleEvent(HighscoresViewModel.Event.NavigateUp)
        }
    )
}

