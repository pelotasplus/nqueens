package pl.pelotasplus.queens.features.select_board_size

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.pelotasplus.queens.data.AvatarRepository
import pl.pelotasplus.queens.domain.model.Avatar
import pl.pelotasplus.queens.navigation.MainDestinations
import javax.inject.Inject

@HiltViewModel
class SelectBoardSizeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val avatarRepository: AvatarRepository
) : ViewModel() {

    private val navArgs by lazy {
        savedStateHandle.toRoute<MainDestinations.SelectBoardSize>()
    }

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleEvent(Event.LoadSelectedAvatar(navArgs.selectedAvatar))
    }

    fun handleEvent(event: Event) {
        when (event) {
            is Event.AvatarSelected -> {
                viewModelScope.launch {
                    _effect.send(Effect.GoToSelectBoardSize(event.avatar))
                }
            }

            is Event.LoadSelectedAvatar -> {
                avatarRepository.getAvatars()
                    .map { avatars -> avatars.first { it.id == event.avatarId } }
                    .onEach { selectedAvatar ->
                        _state.update {
                            it.copy(
                                selectedAvatar = selectedAvatar
                            )
                        }
                    }
                    .launchIn(viewModelScope)
            }
        }
    }

    data class State(
        val selectedAvatar: Avatar? = null,
    )

    sealed interface Effect {
        data class GoToSelectBoardSize(val avatar: Avatar) : Effect
    }

    sealed interface Event {
        data class AvatarSelected(val avatar: Avatar) : Event
        data class LoadSelectedAvatar(val avatarId: Int) : Event
    }
}
