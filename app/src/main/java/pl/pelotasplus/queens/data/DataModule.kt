package pl.pelotasplus.queens.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideHighscoreRepository(@ApplicationContext context: Context): HighscoreDb {
        return Room
            .databaseBuilder(context, HighscoreDb::class.java, "highscores")
            .build()
    }

    @Provides
    fun provideHighscoreDao(highscoreDb: HighscoreDb): HighscoreDao {
        return highscoreDb.highscores()
    }
}
