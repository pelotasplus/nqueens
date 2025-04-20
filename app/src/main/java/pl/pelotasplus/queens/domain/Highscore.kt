package pl.pelotasplus.queens.domain

import java.text.DateFormat
import java.util.Date

data class Highscore(
    val avatar: Avatar,
    val boardSize: Int,
    val startTime: Long,
    val gameTime: Long,
) {
    val prettyTime: String
        get() = DateFormat.getTimeInstance().format(Date(startTime))
}
