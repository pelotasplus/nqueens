package pl.pelotasplus.queens.features.highscores

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.Test
import pl.pelotasplus.queens.MainDispatcherRule
import pl.pelotasplus.queens.data.HighscoreRepository
import pl.pelotasplus.queens.domain.Avatar
import pl.pelotasplus.queens.domain.Highscore
import java.io.IOException

class HighscoresViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val highscores = listOf(
        Highscore(
            avatar = Avatar(1, "Rita", "Bio", 1),
            boardSize = 4,
            startTime = 1,
            gameTime = 1
        )
    )
    private val highscoreRepository = mockk<HighscoreRepository> {
        every { getHighscores() } returns flowOf(highscores)
    }

    private fun createSut() =
        HighscoresViewModel(
            highscoreRepository = highscoreRepository
        )

    @Test
    fun `initial state values`() {
        val sut = createSut()

        assertEquals(emptyList<Highscore>(), sut.state.value.highscores)
        assertEquals(true, sut.state.value.isLoading)
        assertEquals(false, sut.state.value.hasError)
    }

    @Test
    fun `LoadHighscores on init`() {
        val sut = createSut()

        mainDispatcherRule.dispatcher.scheduler.runCurrent()

        assertEquals(highscores, sut.state.value.highscores)
        assertEquals(false, sut.state.value.isLoading)
        assertEquals(false, sut.state.value.hasError)
        verify { highscoreRepository.getHighscores() }
    }

    @Test
    fun `LoadHighscores with exception`() {
        every { highscoreRepository.getHighscores() } returns flow { throw IOException() }

        val sut = createSut()

        mainDispatcherRule.dispatcher.scheduler.runCurrent()

        assertEquals(emptyList<Highscore>(), sut.state.value.highscores)
        assertEquals(false, sut.state.value.isLoading)
        assertEquals(true, sut.state.value.hasError)
        verify { highscoreRepository.getHighscores() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `LoadHighscores multiple calls`() {
        val sut = createSut()

        Dispatchers.setMain(UnconfinedTestDispatcher())

        sut.handleEvent(HighscoresViewModel.Event.LoadHighscores)

        verify(exactly = 2) { highscoreRepository.getHighscores() }

        assertEquals(highscores, sut.state.value.highscores)

        sut.handleEvent(HighscoresViewModel.Event.LoadHighscores)

        verify(exactly = 3) { highscoreRepository.getHighscores() }

        assertEquals(highscores, sut.state.value.highscores)

        every { highscoreRepository.getHighscores() } returns flowOf(emptyList())

        sut.handleEvent(HighscoresViewModel.Event.LoadHighscores)

        verify(exactly = 4) { highscoreRepository.getHighscores() }

        assertEquals(emptyList<Highscore>(), sut.state.value.highscores)
    }

    @Test
    fun `NavigateUp effect`() = runTest {
        val sut = createSut()

        sut.effects.test {
            sut.handleEvent(HighscoresViewModel.Event.NavigateUp)

            assertEquals(HighscoresViewModel.Effect.NavigateUp, awaitItem())
        }
    }
}
