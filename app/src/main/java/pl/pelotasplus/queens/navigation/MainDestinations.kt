package pl.pelotasplus.queens.navigation

import kotlinx.serialization.Serializable

sealed interface MainDestinations {
    @Serializable
    data object PickAvatar : MainDestinations

    @Serializable
    data class SelectBoardSize(
        val selectedAvatar: Int
    ) : MainDestinations

    @Serializable
    data class GameScreen(
        val selectedAvatar: Int = 1,
        val boardSize: Int = 4
    ) : MainDestinations

    @Serializable
    data object Highscores : MainDestinations
}
