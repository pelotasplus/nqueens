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
    data object SelectBoardSize : MainDestinations

    @Serializable
    data object GameScreen : MainDestinations
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
                    navController.navigate(MainDestinations.SelectBoardSize)
                }
            )
        }

        composable<MainDestinations.SelectBoardSize> {
            SelectBoardSizeScreen()
        }

        composable<MainDestinations.GameScreen> {
            GameScreen()
        }
    }
}
