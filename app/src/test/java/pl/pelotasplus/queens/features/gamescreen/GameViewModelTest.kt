package pl.pelotasplus.queens.features.gamescreen

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.pelotasplus.queens.MainDispatcherRule
import pl.pelotasplus.queens.R
import pl.pelotasplus.queens.core.TestCurrentTimeProvider
import pl.pelotasplus.queens.data.AvatarRepository
import pl.pelotasplus.queens.data.HighscoreRepository
import pl.pelotasplus.queens.domain.GameBoardState
import pl.pelotasplus.queens.domain.GridPosition
import pl.pelotasplus.queens.domain.Highscore
import pl.pelotasplus.queens.domain.PositionState
import pl.pelotasplus.queens.domain.PositionState.Queen
import pl.pelotasplus.queens.features.gamescreen.GameViewModel.Effect
import pl.pelotasplus.queens.features.gamescreen.GameViewModel.Event
import pl.pelotasplus.queens.features.gamescreen.GameViewModel.State
import pl.pelotasplus.queens.navigation.MainDestinations

class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val navArgs = MainDestinations.GameScreen(selectedAvatar = 1, boardSize = 4)
    private val savedStateHandle = mockk<SavedStateHandle>()
    private val currentTimeProvider = TestCurrentTimeProvider()
    private val avatarRepository = spyk(AvatarRepository())
    private val highscoreRepository = mockk<HighscoreRepository> {
        every { addHighscore(any()) } returns flowOf(Unit)
    }

    private fun createSut() =
        GameViewModel(
            savedStateHandle = savedStateHandle,
            avatarRepository = avatarRepository,
            highscoreRepository = highscoreRepository,
            currentTimeProvider = currentTimeProvider
        )

    @Before
    fun before() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")

        every { savedStateHandle.toRoute<MainDestinations.GameScreen>() } returns navArgs
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `Initial state setup`() {
        val sut = createSut()

        with(sut.state.value) {
            assertEquals(State.GameStatus.InProgress, gameStatus)
            assertEquals(null, selectedAvatar)
            assertEquals(348753460000L, gameStartTime)
            assertEquals(0L, gameEndTime)
            assertEquals(GameBoardState(4), boardState)
        }
    }

    @Test
    fun `Load avatar on init`() {
        val sut = createSut()

        mainDispatcherRule.dispatcher.scheduler.runCurrent()

        assertEquals(R.drawable.avatar1, sut.state.value.boardState.avatar)
        assertEquals(true, sut.state.value.selectedAvatar != null)

        verify { avatarRepository.getAvatar(1) }
    }

    @Test
    fun `OnTileClicked valid move`() {
        val sut = createSut()

        mainDispatcherRule.dispatcher.scheduler.runCurrent()

        sut.handleEvent(Event.OnTileClicked(GridPosition(0, 0)))

        assertEquals(Queen(false), sut.state.value.boardState.grid[0][0])
    }

    @Test
    fun `OnTileClicked blocked move`() = runTest {
        val sut = createSut()

        mainDispatcherRule.dispatcher.scheduler.runCurrent()

        sut.effect.test {
            sut.handleEvent(Event.OnTileClicked(GridPosition(0, 0)))
            sut.handleEvent(Event.OnTileClicked(GridPosition(0, 1)))

            assertEquals(Queen(true), sut.state.value.boardState.grid[0][0])

            assertEquals(Effect.Vibrate, awaitItem())
        }
    }

    @Test
    fun `OnTileClicked game finished`() = runTest {
        val sut = createSut()

        mainDispatcherRule.dispatcher.scheduler.runCurrent()

        sut.effect.test {
            sut.handleEvent(Event.OnTileClicked(GridPosition(1, 0)))
            sut.handleEvent(Event.OnTileClicked(GridPosition(3, 1)))
            sut.handleEvent(Event.OnTileClicked(GridPosition(0, 2)))
            sut.handleEvent(Event.OnTileClicked(GridPosition(2, 3)))

            assertEquals(Queen(false), sut.state.value.boardState.grid[1][0])
            assertEquals(Queen(false), sut.state.value.boardState.grid[3][1])
            assertEquals(Queen(false), sut.state.value.boardState.grid[0][2])
            assertEquals(Queen(false), sut.state.value.boardState.grid[2][3])

            currentTimeProvider.mockedMillis += 30 * 1000L
            mainDispatcherRule.dispatcher.scheduler.runCurrent()

            assertEquals(
                Effect.ShowFinished(sut.state.value.selectedAvatar!!, 30),
                awaitItem()
            )
            assertEquals(State.GameStatus.Finished, sut.state.value.gameStatus)
            verify {
                highscoreRepository.addHighscore(
                    Highscore(
                        avatar = requireNotNull(sut.state.value.selectedAvatar),
                        boardSize = sut.state.value.boardState.size,
                        startTime = sut.state.value.gameStartTime,
                        gameTime = 30
                    )
                )
            }

            sut.handleEvent(Event.OnTileClicked(GridPosition(0, 1)))

            assertEquals(Queen(false), sut.state.value.boardState.grid[1][0])

            verify(exactly = 1) { highscoreRepository.addHighscore(any()) }
            expectNoEvents()
        }
    }

    @Test
    fun `OnPlayAgainClicked reset state`() = runTest {
        val sut = createSut()

        sut.handleEvent(Event.OnTileClicked(GridPosition(1, 0)))

        assertEquals(Queen(false), sut.state.value.boardState.grid[1][0])
        assertEquals(State.GameStatus.InProgress, sut.state.value.gameStatus)

        sut.handleEvent(Event.OnPlayAgainClicked)

        assertEquals(PositionState.Empty, sut.state.value.boardState.grid[1][0])
        assertEquals(State.GameStatus.InProgress, sut.state.value.gameStatus)
    }

    @Test
    fun `OnAnimationFinished update state`() {
        val sut = createSut()

        sut.handleEvent(Event.OnTileClicked(GridPosition(0, 0)))
        sut.handleEvent(Event.OnTileClicked(GridPosition(0, 1)))

        assertEquals(Queen(shake = true), sut.state.value.boardState.grid[0][0])

        sut.handleEvent(Event.OnAnimationFinished(GridPosition(0, 0)))

        assertEquals(Queen(shake = false), sut.state.value.boardState.grid[0][0])
    }

    @Test
    fun `OnTrophyClicked effect sent`() = runTest {
        val sut = createSut()

        mainDispatcherRule.dispatcher.scheduler.runCurrent()

        sut.effect.test {
            sut.handleEvent(Event.OnTrophyClicked)

            assertEquals(Effect.ShowHighscores, awaitItem())
        }
    }
}
