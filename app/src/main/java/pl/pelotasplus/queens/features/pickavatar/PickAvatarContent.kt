package pl.pelotasplus.queens.features.pickavatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.domain.Avatar
import pl.pelotasplus.queens.ui.theme.NQueensTheme
import kotlin.math.absoluteValue

@Composable
internal fun PickAvatarContent(
    state: PickAvatarViewModel.State,
    modifier: Modifier = Modifier,
    onAvatarSelect: (Avatar) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { state.avatars.size })
    var selectedAvatar by remember { mutableStateOf<Avatar?>(null) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedAvatar = state.avatars[page]
        }
    }

    Column(
        modifier = modifier
    ) {
        Text(
            text = "Select your avatar",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.Companion.CenterHorizontally)
                .padding(16.dp)
        )

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 100.dp),
            modifier = Modifier.weight(0.5f)
        ) { currentPage ->
            val avatar = state.avatars[currentPage]
            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false),
                    onClick = {
                        selectedAvatar?.let { onAvatarSelect(it) }
                    }
                )
            ) {
                Image(
                    painter = painterResource(id = avatar.image),
                    contentDescription = avatar.bio,
                    modifier = Modifier
                        .align(Alignment.Companion.Center)
                        .clip(RoundedCornerShape(16.dp))
                        .graphicsLayer {
                            val pageOffset = ((pagerState.currentPage - currentPage)
                                    + pagerState.currentPageOffsetFraction).absoluteValue

                            lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale
                            }

                            alpha = lerp(
                                start = 0.75f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )

                        }
                        .fillMaxSize()
                )
            }
        }

        Column(modifier = Modifier.weight(0.5f)) {
            Text(
                text = selectedAvatar?.name.orEmpty(),
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .align(Alignment.Companion.CenterHorizontally)
                    .padding(16.dp)
            )

            Text(
                text = selectedAvatar?.bio.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Companion.Center,
                modifier = Modifier
                    .align(Alignment.Companion.CenterHorizontally)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .align(Alignment.Companion.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(32.dp),
                onClick = {
                    selectedAvatar?.let { onAvatarSelect(it) }
                }
            ) {
                Text(
                    "Pick ${selectedAvatar?.name.orEmpty()}!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PickAvatarContentPreview() {
    NQueensTheme {
        PickAvatarContent(
            state = PickAvatarViewModel.State(
                avatars = listOf(
                    Avatar(
                        id = 1,
                        name = "Rita",
                        bio = "Rita is the wise queen of the garden kingdom. She spends her days watching butterflies and giving advice to lost bugs. Likes: Belly rubs, Sunny naps. Dislikes: Loud thunder, Soggy grass.",
                        image = R.drawable.avatar1
                    ),
                    Avatar(
                        id = 2,
                        name = "Lola",
                        bio = "Lola is the fastest tail-wagger in the land! She's always ready for an adventure, especially if it involves snacks. Likes: Cheese cubes, Chasing her tail. Dislikes: Vacuums, Bedtime.",
                        image = R.drawable.avatar2
                    ),
                )
            )
        )
    }
}
