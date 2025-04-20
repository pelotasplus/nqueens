package pl.pelotasplus.queens.features.highscores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.pelotasplus.queens.data.HighscoreRepository
import pl.pelotasplus.queens.domain.Highscore
import javax.inject.Inject

@HiltViewModel
class HighscoresViewModel @Inject constructor(
    private val highscoreRepository: HighscoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _effect = Channel<Effect>()
    val effects = _effect.receiveAsFlow()

    init {
        handleEvent(Event.LoadHighscores)
    }

    fun handleEvent(event: Event) {
        when (event) {
            Event.LoadHighscores -> {
                highscoreRepository.getHighscores()
                    .onEach { highscores ->
                        _state.update {
                            it.copy(
                                highscores = highscores
                            )
                        }
                    }
                    .onCompletion {
                        _state.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                    }
                    .launchIn(viewModelScope)
            }

            Event.NavigateUp -> {
                viewModelScope.launch {
                    _effect.send(Effect.NavigateUp)
                }
            }
        }
    }

    data class State(
        val highscores: List<Highscore> = emptyList(),
        val isLoading: Boolean = true
    )

    sealed interface Effect {
        data object NavigateUp : Effect
    }

    sealed interface Event {
        data object NavigateUp : Event
        data object LoadHighscores : Event
    }
}
