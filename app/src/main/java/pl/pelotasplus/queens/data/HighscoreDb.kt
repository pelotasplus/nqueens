package pl.pelotasplus.queens.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HighscoreData::class], version = 1)
abstract class HighscoreDb : RoomDatabase() {
    abstract fun highscores(): HighscoreDao
}
