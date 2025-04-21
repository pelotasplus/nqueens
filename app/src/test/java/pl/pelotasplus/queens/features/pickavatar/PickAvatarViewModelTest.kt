package pl.pelotasplus.queens.features.pickavatar

import app.cash.turbine.test
import io.mockk.coVerify
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import pl.pelotasplus.queens.MainDispatcherRule
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.data.AvatarRepository
import pl.pelotasplus.queens.domain.Avatar

@OptIn(ExperimentalCoroutinesApi::class)
class PickAvatarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val avatarRepository = spyk(AvatarRepository())
    private fun createSut() =
        PickAvatarViewModel(
            avatarRepository = avatarRepository
        )

    @Test
    fun `initial state check`() {
        val sut = createSut()

        assertEquals(0, sut.state.value.avatars.size)
    }

    @Test
    fun `LoadAvatars on init`() {
        val sut = createSut()

        mainDispatcherRule.dispatcher.scheduler.runCurrent()

        assertEquals(4, sut.state.value.avatars.size)
        assertEquals("Rita", sut.state.value.avatars.first().name)

        coVerify { avatarRepository.getAvatars() }
    }

    @Test
    fun `AvatarSelected event effect emission check`() = runTest {
        val avatar = Avatar(
            id = 1,
            name = "Rita",
            bio = "Rita is the wise queen of the garden kingdom. She spends her days watching butterflies and giving advice to lost bugs. Likes: Belly rubs, Sunny naps. Dislikes: Loud thunder, Soggy grass.",
            image = R.drawable.avatar1
        )

        val sut = createSut()

        sut.effect.test {
            sut.handleEvent(PickAvatarViewModel.Event.AvatarSelected(avatar))

            assertEquals(PickAvatarViewModel.Effect.GoToSelectBoardSize(avatar), awaitItem())
        }
    }
}
