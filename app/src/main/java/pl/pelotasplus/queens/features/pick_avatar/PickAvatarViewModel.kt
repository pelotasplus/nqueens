package pl.pelotasplus.queens.features.pick_avatar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.pelotasplus.queens.data.AvatarRepository
import pl.pelotasplus.queens.domain.model.Avatar
import javax.inject.Inject

@HiltViewModel
class PickAvatarViewModel @Inject constructor(
    private val avatarRepository: AvatarRepository
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleEvent(Event.LoadAvatars)
    }

    fun handleEvent(event: Event) {
        when (event) {
            is Event.AvatarSelected -> {
                viewModelScope.launch {
                    _effect.send(Effect.GoToSelectBoardSize(event.avatar))
                }
            }

            Event.LoadAvatars -> {
                loadAvatars()
            }
        }
    }

    private fun loadAvatars() {
        avatarRepository.getAvatars()
            .onEach { avatars ->
                _state.update {
                    it.copy(
                        avatars = avatars
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    data class State(
        val avatars: List<Avatar> = emptyList()
    )

    sealed interface Effect {
        data class GoToSelectBoardSize(val avatar: Avatar) : Effect
    }

    sealed interface Event {
        data class AvatarSelected(val avatar: Avatar) : Event
        data object LoadAvatars: Event
    }
}
