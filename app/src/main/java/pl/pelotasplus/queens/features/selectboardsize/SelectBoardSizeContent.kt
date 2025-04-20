package pl.pelotasplus.queens.features.selectboardsize

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.domain.GameBoardState
import pl.pelotasplus.queens.domain.Avatar
import pl.pelotasplus.queens.ui.composable.GameBoard
import pl.pelotasplus.queens.ui.theme.NQueensTheme
import kotlin.math.abs

private const val MIN_BOARD_SIZE = 4f
private const val MAX_BOARD_SIZE = 8f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectBoardSizeContent(
    state: SelectBoardSizeViewModel.State,
    modifier: Modifier = Modifier,
    onBoardSizeSelect: (Int) -> Unit = {}
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
                .align(Alignment.Companion.CenterHorizontally)
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
                .align(Alignment.Companion.CenterHorizontally)
                .fillMaxWidth()
                .padding(32.dp),
            onClick = {
                onBoardSizeSelect(sliderPosition.toInt())
            }
        ) {
            Text(
                "Pick ${sliderPosition.toInt()}x${sliderPosition.toInt()}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Suppress("MagicNumber")
private fun calculateRotation(position: Float): Float {
    val minRotation = -30f
    val maxRotation = minRotation * -1
    val distance = maxRotation + abs(minRotation)
    val steps = MAX_BOARD_SIZE - MIN_BOARD_SIZE
    val stepSize = distance / steps
    return minRotation + (position - MIN_BOARD_SIZE) * stepSize
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
