package pl.pelotasplus.queens.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pl.pelotasplus.queens.features.gamescreen.GameScreen
import pl.pelotasplus.queens.features.highscores.HighscoresScreen
import pl.pelotasplus.queens.features.pickavatar.PickAvatarScreen
import pl.pelotasplus.queens.features.selectboardsize.SelectBoardSizeScreen

@Composable
fun MainNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier.padding(paddingValues),
        navController = navController,
        startDestination = MainDestinations.GameScreen(1, 4)
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
            GameScreen(
                goToHighscores = {
                    navController.navigate(MainDestinations.Highscores)
                }
            )
        }

        composable<MainDestinations.Highscores> {
            HighscoresScreen(
                onNavigateUp = {
                    navController.navigateUp()
                }
            )
        }
    }
}
