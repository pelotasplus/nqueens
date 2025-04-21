package pl.pelotasplus.queens.features.selectboardsize

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.pelotasplus.queens.MainDispatcherRule
import pl.pelotasplus.queens.data.AvatarRepository
import pl.pelotasplus.queens.navigation.MainDestinations
import pl.pelotasplus.queens.navigation.MainDestinations.SelectBoardSize

class SelectBoardSizeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val navArgs = SelectBoardSize(selectedAvatar = 1)
    private val savedStateHandle = mockk<SavedStateHandle>()
    private val avatarRepository = spyk(AvatarRepository())

    private fun createSut() =
        SelectBoardSizeViewModel(
            savedStateHandle = savedStateHandle,
            avatarRepository = avatarRepository
        )

    @Before
    fun before() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")

        every { savedStateHandle.toRoute<MainDestinations.SelectBoardSize>() } returns navArgs
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `initial state check`() {
        val sut = createSut()

        assertEquals(null, sut.state.value.selectedAvatar)
    }

    @Test
    fun `LoadSelectedAvatar on init`() {
        val sut = createSut()

        mainDispatcherRule.dispatcher.scheduler.runCurrent()

        assertEquals(1, sut.state.value.selectedAvatar!!.id)
        assertEquals("Rita", sut.state.value.selectedAvatar!!.name)

        coVerify { avatarRepository.getAvatar(1) }
    }

    @Test
    fun `LoadSelectedAvatar event successful load`() {
        val sut = createSut()

        sut.handleEvent(SelectBoardSizeViewModel.Event.LoadSelectedAvatar(2))

        mainDispatcherRule.dispatcher.scheduler.runCurrent()

        assertEquals(2, sut.state.value.selectedAvatar!!.id)
        assertEquals("Lola", sut.state.value.selectedAvatar!!.name)

        verify { avatarRepository.getAvatar(2) }
    }

    @Test
    fun `LoadSelectedAvatar event with invalid id`() = runTest {
        val sut = createSut()

        sut.effect.test {
            sut.handleEvent(SelectBoardSizeViewModel.Event.LoadSelectedAvatar(99))

            verify { avatarRepository.getAvatar(99) }

            assertEquals(SelectBoardSizeViewModel.Effect.NavigateUp, awaitItem())
        }
    }

    @Test
    fun `OnBoardSizeSelected event effect emission`() = runTest {
        val sut = createSut()

        sut.effect.test {
            sut.handleEvent(SelectBoardSizeViewModel.Event.OnBoardSizeSelected(4))

            mainDispatcherRule.dispatcher.scheduler.runCurrent()

            assertEquals(
                SelectBoardSizeViewModel.Effect.StartGame(
                    avatar = sut.state.value.selectedAvatar!!,
                    size = 4
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `OnNavigateUp event effect emission`() = runTest {
        val sut = createSut()

        sut.effect.test {
            sut.handleEvent(SelectBoardSizeViewModel.Event.OnNavigateUp)

            assertEquals(SelectBoardSizeViewModel.Effect.NavigateUp, awaitItem())
        }
    }
}
