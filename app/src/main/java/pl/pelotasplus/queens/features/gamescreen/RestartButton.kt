package pl.pelotasplus.queens.features.gamescreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.pelotasplus.queens.R

@Composable
internal fun RestartButton(
    onRetryClick: () -> Unit = {}
) {
    val rotation = remember { Animatable(0f) }
    var trigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(trigger) {
        if (trigger > 0) {
            rotation.animateTo(
                targetValue = -360f * trigger,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessVeryLow
                )
            )
        }
    }

    Image(
        painter = painterResource(R.drawable.retry),
        modifier = Modifier
            .size(80.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false),
                onClick = {
                    trigger++
                    onRetryClick()
                }
            )
            .graphicsLayer(
                rotationZ = rotation.value
            ),
        contentDescription = null
    )
}
