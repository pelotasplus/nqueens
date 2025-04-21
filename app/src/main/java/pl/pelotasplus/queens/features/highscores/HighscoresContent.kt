package pl.pelotasplus.queens.features.highscores

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.domain.Avatar
import pl.pelotasplus.queens.domain.Highscore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighscoresContent(
    state: HighscoresViewModel.State,
    modifier: Modifier = Modifier,
    onNavigateUpClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Highscores")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUpClick) {
                        Image(
                            painter = painterResource(R.drawable.go_back),
                            contentDescription = "Go back"
                        )
                    }
                },
            )
        },
    ) {
        if (state.isLoading) {
            HighscoresContentLoading(Modifier.padding(it))
        } else if (state.hasError) {
            HighscoresContentError(Modifier.padding(it))
        } else {
            HighscoresContentLoaded(state, Modifier.padding(it))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HighscoresContentPreview() {
    HighscoresContent(
        state = HighscoresViewModel.State(
            highscores = listOf(
                Highscore(
                    Avatar(
                        id = 1,
                        name = "Rita",
                        bio = "Rita is the wise queen of the garden kingdom. She spends her days watching butterflies and giving advice to lost bugs.\nLikes: Belly rubs, Sunny naps.\nDislikes: Loud thunder, Soggy grass.",
                        image = R.drawable.avatar1
                    ),
                    boardSize = 8,
                    startTime = 1709048734,
                    gameTime = 40L
                )
            )
        )
    )
}
