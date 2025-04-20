package pl.pelotasplus.queens.features.game_screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.lerp
import pl.pelotasplus.queens.core.PositionState


@Composable
internal fun ShakingImage(
    queen: PositionState.Queen,
    @DrawableRes imageResId: Int,
    modifier: Modifier = Modifier,
    onAnimationFinished: () -> Unit = {}
) {
    var triggerAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(queen) {
        if (queen.shake) {
            triggerAnimation = true
        }
    }

    val animDuration = 500

    // rotate -15 to 15 degrees
    val rotation by animateFloatAsState(
        targetValue = if (triggerAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = animDuration),
        label = "rotation",
        finishedListener = {
            if (triggerAnimation == true) {
                onAnimationFinished()
            }
            triggerAnimation = false
        }
    )

    // scale up and down
    val scale by animateFloatAsState(
        targetValue = if (triggerAnimation) 1.25f else 1f,
        animationSpec = tween(durationMillis = animDuration),
        label = "scale"
    )

    // map progress from 0 to 1 and then from 1 to 0 to some rotation degress
    val currentRotation = when {
        rotation < 0.2f -> lerp(0f, -15f, rotation * 5)
        rotation < 0.4f -> lerp(-15f, 0f, (rotation - 0.2f) * 5)
        rotation < 0.6f -> lerp(0f, 15f, (rotation - 0.4f) * 5)
        rotation < 1.0f -> lerp(15f, 0f, (rotation - 0.6f) * 2.5f)
        else -> 0f
    }

    Image(
        painter = painterResource(id = imageResId),
        contentDescription = null,
        modifier = modifier
            .graphicsLayer(
                clip = true,
                rotationZ = currentRotation,
                scaleX = scale,
                scaleY = scale
            )
    )
}
