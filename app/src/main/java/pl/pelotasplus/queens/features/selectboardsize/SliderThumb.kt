package pl.pelotasplus.queens.features.selectboardsize

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.pelotasplus.queens.R

@Composable
internal fun SliderThumb(
    @DrawableRes avatar: Int,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
) {
    val interactions = remember { mutableStateListOf<Interaction>() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactions.add(interaction)
                is PressInteraction.Release -> interactions.remove(interaction.press)
                is PressInteraction.Cancel -> interactions.remove(interaction.press)
                is DragInteraction.Start -> interactions.add(interaction)
                is DragInteraction.Stop -> interactions.remove(interaction.start)
                is DragInteraction.Cancel -> interactions.remove(interaction.start)
            }
        }
    }

    Box(
        modifier = modifier
            .hoverable(interactionSource = interactionSource)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
            .size(36.dp)
    ) {
        Image(
            painter = painterResource(id = avatar),
            contentDescription = null,
            modifier = Modifier
                .scale(1.6f)
                .align(Alignment.Companion.Center)
        )
    }
}

@Preview
@Composable
private fun SliderThumbPreview() {
    SliderThumb(
        avatar = R.drawable.avatar1,
        interactionSource = remember { MutableInteractionSource() }
    )
}
