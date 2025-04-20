package pl.pelotasplus.queens.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HighscoreDao {
    @Query("SELECT * FROM highscores ORDER BY startTime DESC")
    suspend fun getAll(): List<HighscoreData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(highscoreData: HighscoreData)
}
