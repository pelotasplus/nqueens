package pl.pelotasplus.queens.features.highscores

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.pelotasplus.queens.R

@Composable
internal fun HighscoresContentLoading(
    modifier: Modifier = Modifier,
) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = -360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box(modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.retry),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(80.dp)
                .graphicsLayer(
                    rotationZ = rotation.value
                )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HighscoresContentLoadingPreview() {
    HighscoresContentLoading()
}
