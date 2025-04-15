package pl.pelotasplus.queens.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import pl.pelotasplus.queens.features.game_screen.GameScreen
import pl.pelotasplus.queens.features.pick_avatar.PickAvatarScreen
import pl.pelotasplus.queens.features.select_board_size.SelectBoardSizeScreen

sealed interface MainDestinations {
    @Serializable
    data object PickAvatar : MainDestinations

    @Serializable
    data class SelectBoardSize(
        val selectedAvatar: Int
    ) : MainDestinations

    @Serializable
    data class GameScreen(
        val selectedAvatar: Int,
        val boardSize: Int
    ) : MainDestinations
}

@Composable
fun MainNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        modifier = Modifier.Companion.padding(paddingValues),
        navController = navController,
        startDestination = MainDestinations.PickAvatar
    ) {
        composable<MainDestinations.PickAvatar> {
            PickAvatarScreen(
                goToSelectBoardSize = {
                    navController.navigate(
                        MainDestinations.SelectBoardSize(
                            selectedAvatar = it.id
                        )
                    )
                }
            )
        }

        composable<MainDestinations.SelectBoardSize> {
            SelectBoardSizeScreen(
                startGame = { avatar, size ->
                    navController.navigate(
                        MainDestinations.GameScreen(
                            selectedAvatar = avatar.id,
                            boardSize = size
                        )
                    )
                }
            )
        }

        composable<MainDestinations.GameScreen> {
            GameScreen()
        }
    }
}
