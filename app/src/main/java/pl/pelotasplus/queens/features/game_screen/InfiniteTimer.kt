package pl.pelotasplus.queens.features.game_screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import pl.pelotasplus.queens.core.formatTime

@Composable
internal fun InfiniteTimer(
    isRunning: Boolean,
    startTime: Long,
    modifier: Modifier = Modifier,
) {
    var seconds by remember { mutableLongStateOf(0) }

    LaunchedEffect(startTime) {
        seconds = 0
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            seconds++
        }
    }

    Text(
        modifier = modifier,
        text = formatTime(seconds),
        style = MaterialTheme.typography.headlineMedium,
    )
}
