package pl.pelotasplus.queens.features.select_board_size

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.core.ObserveEffects
import pl.pelotasplus.queens.domain.model.Avatar
import pl.pelotasplus.queens.ui.composable.GameBoard
import pl.pelotasplus.queens.ui.composable.GameBoardState
import pl.pelotasplus.queens.ui.theme.NQueensTheme
import kotlin.math.abs

@Composable
fun SelectBoardSizeScreen(
    modifier: Modifier = Modifier,
    viewModel: SelectBoardSizeViewModel = hiltViewModel(),
    startGame: (Avatar, Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffects(viewModel.effect) { effect ->
        when (effect) {
            is SelectBoardSizeViewModel.Effect.StartGame -> {
                startGame(effect.avatar, effect.size)
            }
        }
    }

    SelectBoardSizeContent(
        modifier = modifier,
        state = state,
        onBoardSizeSelected = {
            viewModel.handleEvent(SelectBoardSizeViewModel.Event.OnBoardSizeSelected(it))
        }
    )
}

private const val MIN_BOARD_SIZE = 4f
private const val MAX_BOARD_SIZE = 8f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBoardSizeContent(
    state: SelectBoardSizeViewModel.State,
    modifier: Modifier = Modifier,
    onBoardSizeSelected: (Int) -> Unit = {}
) {
    var sliderPosition by remember { mutableFloatStateOf(MIN_BOARD_SIZE) }
    var rotation by remember { mutableFloatStateOf(calculateRotation(sliderPosition)) }

    Column(
        modifier = modifier
    ) {
        Text(
            text = "Select board size",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )

        GameBoard(
            state = GameBoardState(
                size = sliderPosition.toInt()
            )
        )

        Spacer(Modifier.weight(1f))

        val interactionSource = remember { MutableInteractionSource() }
        Slider(
            interactionSource = interactionSource,
            value = sliderPosition,
            valueRange = MIN_BOARD_SIZE..MAX_BOARD_SIZE,
            steps = MAX_BOARD_SIZE.toInt() - MIN_BOARD_SIZE.toInt() - 1,
            modifier = Modifier.padding(32.dp),
            onValueChange = {
                sliderPosition = it
                rotation = calculateRotation(it)
            },
            thumb = {
                SliderThumb(
                    avatar = state.selectedAvatar?.image ?: R.drawable.avatar1,
                    interactionSource = interactionSource,
                    modifier = Modifier.rotate(rotation)
                )
            }
        )

        Spacer(Modifier.weight(0.1f))

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(32.dp),
            onClick = {
                onBoardSizeSelected(sliderPosition.toInt())
            }
        ) {
            Text(
                "Pick ${sliderPosition.toInt()}x${sliderPosition.toInt()}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun calculateRotation(position: Float): Float {
    val minRotation = -30f
    val maxRotation = minRotation * -1
    val distance = maxRotation + abs(minRotation)
    val steps = MAX_BOARD_SIZE - MIN_BOARD_SIZE
    val stepSize = distance / steps
    return minRotation + (position - MIN_BOARD_SIZE) * stepSize
}

@Composable
private fun SliderThumb(
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
                .align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SelectBoardSizeContentPreview() {
    NQueensTheme {
        SelectBoardSizeContent(
            state = SelectBoardSizeViewModel.State(
                selectedAvatar = Avatar(
                    id = 1,
                    name = "Rita",
                    bio = "Rita is the wise queen of the garden kingdom. She spends her days watching butterflies and giving advice to lost bugs.\nLikes: Belly rubs, Sunny naps.\nDislikes: Loud thunder, Soggy grass.",
                    image = R.drawable.avatar1
                )
            ),
        )
    }
}
