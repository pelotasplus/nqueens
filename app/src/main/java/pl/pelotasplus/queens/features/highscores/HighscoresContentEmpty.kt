package pl.pelotasplus.queens.features.highscores

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.pelotasplus.queens.R

@Composable
internal fun HighscoresContentEmpty(
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(32.dp)) {
        Image(
            painter = painterResource(R.drawable.empty),
            contentDescription = null,
            modifier = Modifier
                .width(200.dp)
                .padding(bottom = 64.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            stringResource(R.string.highscores_empty),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun HighscoresContentEmptyPreview() {
    HighscoresContentEmpty()
}
