package pl.pelotasplus.queens.features.highscores

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import pl.pelotasplus.queens.MainDispatcherRule
import pl.pelotasplus.queens.data.HighscoreRepository
import pl.pelotasplus.queens.domain.Avatar
import pl.pelotasplus.queens.domain.Highscore

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

        assertEquals(highscores, sut.state.value.highscores)

        verify { highscoreRepository.getHighscores() }
    }

    @Test
    fun `LoadHighscores multiple calls`() {
        val sut = createSut()

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
