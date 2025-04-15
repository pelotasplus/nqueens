package pl.pelotasplus.queens.features.game_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import pl.pelotasplus.queens.data.AvatarRepository
import pl.pelotasplus.queens.domain.model.Avatar
import pl.pelotasplus.queens.navigation.MainDestinations
import pl.pelotasplus.queens.ui.composable.GameBoardPosition
import pl.pelotasplus.queens.ui.composable.GameBoardState
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val avatarRepository: AvatarRepository
) : ViewModel() {

    private val navArgs by lazy {
        savedStateHandle.toRoute<MainDestinations.GameScreen>()
    }

    private val _state = MutableStateFlow(
        State(
            boardState = GameBoardState(size = navArgs.boardSize),
            gameStartTime = System.currentTimeMillis()
        )
    )
    val state = _state.asStateFlow()

    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleEvent(Event.LoadSelectedAvatar(navArgs.selectedAvatar))
    }

    fun handleEvent(event: Event) {
        when (event) {
            is Event.LoadSelectedAvatar -> {
                avatarRepository.getAvatar(event.avatarId)
                    .onEach { selectedAvatar ->
                        _state.update {
                            it.copy(
                                boardState = it.boardState.copy(
                                    avatar = selectedAvatar.image
                                ),
                                selectedAvatar = selectedAvatar
                            )
                        }
                    }
                    .launchIn(viewModelScope)
            }

            is Event.OnTileClicked -> {
                _state.update {
                    it.copy(
                        boardState = it.boardState.copy(
                            pieces = if (it.boardState.pieces.contains(event.position)) {
                                it.boardState.pieces - event.position
                            } else if (it.boardState.movesLeft > 0) {
                                it.boardState.pieces + event.position
                            } else {
                                it.boardState.pieces
                            }
                        )
                    )
                }
            }

            Event.OnRetryClicked -> {
                _state.update {
                    it.copy(
                        boardState = it.boardState.copy(
                            pieces = emptySet()
                        ),
                        gameStartTime = System.currentTimeMillis()
                    )
                }
            }
        }

    }

    data class State(
        val boardState: GameBoardState,
        val selectedAvatar: Avatar? = null,
        val gameStartTime: Long = 0L,
    )

    sealed interface Effect {
    }

    sealed interface Event {
        data class LoadSelectedAvatar(val avatarId: Int) : Event
        data class OnTileClicked(val position: GameBoardPosition) : Event
        data object OnRetryClicked : Event
    }
}
