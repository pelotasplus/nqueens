package pl.pelotasplus.queens.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun <Effect> ObserveEffects(
    effects: Flow<Effect>,
    onEffect: suspend (Effect) -> Unit
) {
    val lifecycleObserver = LocalLifecycleOwner.current
    val latestCallback by rememberUpdatedState(onEffect)
    LaunchedEffect(effects, lifecycleObserver.lifecycle) {
        lifecycleObserver.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                effects.collect { latestCallback(it) }
            }
        }
    }
}
