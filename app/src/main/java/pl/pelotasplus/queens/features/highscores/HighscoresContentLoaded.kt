package pl.pelotasplus.queens.features.highscores

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.core.formatTime
import pl.pelotasplus.queens.domain.Avatar
import pl.pelotasplus.queens.domain.Highscore

@Composable
internal fun HighscoresContentLoaded(
    state: HighscoresViewModel.State,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(state.highscores) { highscore ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Row(
                    Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(highscore.avatar.image),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(40.dp)
                    )
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp)
                    ) {
                        Row(Modifier.weight(1f)) {
                            Text(
                                highscore.avatar.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .weight(1f)
                            )
                            Text(
                                highscore.prettyTime,
                            )
                        }
                        Text(
                            stringResource(
                                R.string.highscores_message,
                                highscore.boardSize,
                                highscore.boardSize,
                                formatTime(
                                    highscore.gameTime
                                )
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HighscoresContentLoadedPreview() {
    HighscoresContentLoaded(
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
