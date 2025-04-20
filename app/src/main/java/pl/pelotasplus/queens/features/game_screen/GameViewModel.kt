package pl.pelotasplus.queens.features.game_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.pelotasplus.queens.data.AvatarRepository
import pl.pelotasplus.queens.domain.model.Avatar
import pl.pelotasplus.queens.navigation.MainDestinations
import pl.pelotasplus.queens.ui.composable.GameBoardPosition
import pl.pelotasplus.queens.ui.composable.GameBoardPositionState.BlockedBy
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

        _state.map {
            it.boardState.movesLeft to it.selectedAvatar
        }.filter { (movesLeft, avatar) ->
            movesLeft == 0 && avatar != null
        }.onEach { (movesLeft, avatar) ->
            _effect.send(
                Effect.ShowFinished(
                    Effect.ShowFinished.WinnerDetails(
                        avatar!!,
                        "12:34"
                    )
                )
            )
        }.launchIn(viewModelScope)
    }

    fun handleEvent(event: Event) {
        println("XXX handleEvent $event")
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
                val newState =
                    _state.value.boardState.handleClick(event.position.row, event.position.col)

                val positionState = newState.grid[event.position.row][event.position.col]
                if (positionState is BlockedBy) {
                    viewModelScope.launch {
                        _effect.send(Effect.Vibrate)
                    }
                }

                newState.dump()

                _state.update {
                    it.copy(
                        boardState = newState,
                    )
                }
            }

            Event.OnPlayAgainClicked -> {
                val size = _state.value.boardState.size
                _state.update {
                    it.copy(
                        boardState = it.boardState.emptyGrid(),
                        gameStartTime = System.currentTimeMillis()
                    )
                }
            }

            is Event.OnAnimationFinished -> {
                println("XXX animation finshied for ${event.position.row} x ${event.position.col}")

                println("XXX state before")
                _state.value.boardState.dump()

                val newState = _state.value.boardState.shakeQueen(
                    listOf(
                        GameBoardPosition(
                            event.position.row,
                            event.position.col
                        )
                    ),
                    false
                )

                println("XXX after update to false")
                newState.dump()

                _state.update {
                    it.copy(
                        boardState = newState,
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
        object Vibrate : Effect
        data class ShowFinished(
            val winnerDetails: WinnerDetails,
        ) : Effect {
            data class WinnerDetails(
                val avatar: Avatar,
                val timeElapsed: String
            )
        }
    }

    sealed interface Event {
        data object OnPlayAgainClicked : Event
        data class LoadSelectedAvatar(val avatarId: Int) : Event
        data class OnTileClicked(val position: GameBoardPosition) : Event
        data class OnAnimationFinished(val position: GameBoardPosition) : Event
    }
}
