package pl.pelotasplus.queens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.pelotasplus.queens.navigation.MainNavigation
import pl.pelotasplus.queens.ui.theme.NQueensTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NQueensTheme {
                MainNavigation(
                )
            }
        }
    }
}
