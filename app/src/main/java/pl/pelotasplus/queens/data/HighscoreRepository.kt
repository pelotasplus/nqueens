package pl.pelotasplus.queens.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import pl.pelotasplus.queens.domain.Highscore
import javax.inject.Inject

class HighscoreRepository @Inject constructor(
    private val highscoreDao: HighscoreDao,
    private val avatarRepository: AvatarRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getHighscores(): Flow<List<Highscore>> {
        return avatarRepository.getAvatars()
            .map { avatars ->
                highscoreDao.getAll().map { highscoreData ->
                    highscoreData.toHighscore(avatars.first { it.id == highscoreData.avatarId })
                }
            }
    }

    fun addHighscore(highscore: Highscore): Flow<Unit> {
        return flow {
            highscoreDao.upsert(highscore.toHighscoreData())
            emit(Unit)
        }
    }
}
