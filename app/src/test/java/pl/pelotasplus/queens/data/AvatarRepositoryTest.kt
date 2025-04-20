package pl.pelotasplus.queens.data

import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AvatarRepositoryTest {

    private val sut = AvatarRepository()

    @Test
    fun `returns avatars`() = runTest {
        sut.getAvatars().test {
            val avatars = awaitItem()
            assertEquals(4, avatars.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `returns matching avatar`() = runTest {
        sut.getAvatar(1).test {
            val avatar = awaitItem()
            assertEquals("Rita", avatar.name)
            assertEquals(1, avatar.id)
            cancelAndIgnoreRemainingEvents()
        }

        sut.getAvatar(4).test {
            val avatar = awaitItem()
            assertEquals("Pablo", avatar.name)
            assertEquals(4, avatar.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `errors out on invalid avatar id`() = runTest {
        sut.getAvatar(5).test {
            awaitError()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
