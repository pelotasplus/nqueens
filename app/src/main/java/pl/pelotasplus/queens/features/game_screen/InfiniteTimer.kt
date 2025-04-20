package pl.pelotasplus.queens.features.game_screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun InfiniteTimer(
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

fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)
}
