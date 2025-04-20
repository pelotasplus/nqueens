package pl.pelotasplus.queens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.pelotasplus.queens.features.gamescreen.GameScreen
import pl.pelotasplus.queens.features.highscores.HighscoresScreen
import pl.pelotasplus.queens.features.pickavatar.PickAvatarScreen
import pl.pelotasplus.queens.features.selectboardsize.SelectBoardSizeScreen

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
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
                },
                navigateUp = {
                    navController.navigateUp()
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
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }
    }
}
