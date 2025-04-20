package pl.pelotasplus.queens.data

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import pl.pelotasplus.queens.domain.Avatar
import pl.pelotasplus.queens.domain.Highscore

class HighscoreRepositoryTest {

    private val highscoreDao = mockk<HighscoreDao>(relaxed = true)
    private val avatarRepository = AvatarRepository()
    private val sut = HighscoreRepository(
        highscoreDao = highscoreDao,
        avatarRepository = avatarRepository
    )

    @Test
    fun `returns highscores`() = runTest {
        val highscoreData = listOf(
            HighscoreData(1, 1, 4, 1),
            HighscoreData(2, 2, 5, 2),
            HighscoreData(3, 3, 6, 3),
            HighscoreData(4, 4, 7, 4),
        )
        coEvery { highscoreDao.getAll() } returns highscoreData

        sut.getHighscores().test {
            val highscores = awaitItem()
            assertEquals(4, highscores.size)

            assertEquals("Rita", highscores[0].avatar.name)
            assertEquals("Lola", highscores[1].avatar.name)
            assertEquals("Jack", highscores[2].avatar.name)
            assertEquals("Pablo", highscores[3].avatar.name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `adds highscore`() = runTest {
        val highscore = Highscore(
            avatar = Avatar(1, "Rita", "Bio", 1),
            boardSize = 4,
            startTime = 1,
            gameTime = 1
        )

        sut.addHighscore(highscore).test {
            coVerify {
                highscoreDao.upsert(highscore.toHighscoreData())
            }
            assertEquals(Unit, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

}
