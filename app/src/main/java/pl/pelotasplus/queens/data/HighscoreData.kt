package pl.pelotasplus.queens.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.pelotasplus.queens.domain.Avatar
import pl.pelotasplus.queens.domain.Highscore

@Entity(tableName = "highscores")
data class HighscoreData(
    @PrimaryKey val startTime: Long,
    @ColumnInfo(name = "avatarId") val avatarId: Int,
    @ColumnInfo(name = "size") val boardSize: Int,
    @ColumnInfo(name = "gameTime") val gameTime: Long,
)

fun HighscoreData.toHighscore(avatar: Avatar): Highscore {
    return Highscore(
        avatar = avatar,
        boardSize = boardSize,
        startTime = startTime,
        gameTime = gameTime
    )
}

fun Highscore.toHighscoreData(): HighscoreData {
    return HighscoreData(
        avatarId = avatar.id,
        boardSize = boardSize,
        startTime = startTime,
        gameTime = gameTime
    )
}
