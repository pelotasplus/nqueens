package pl.pelotasplus.queens.features.game_screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun InfiniteTimer(
    startTime: Long,
    modifier: Modifier = Modifier,
) {
    var seconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(startTime) {
        seconds = 0
        while (true) {
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

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}
