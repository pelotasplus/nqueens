package pl.pelotasplus.queens.features.gamescreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.pelotasplus.queens.data.AvatarRepository
import pl.pelotasplus.queens.data.HighscoreRepository
import pl.pelotasplus.queens.domain.Avatar
import pl.pelotasplus.queens.domain.GameBoardState
import pl.pelotasplus.queens.domain.GridPosition
import pl.pelotasplus.queens.domain.Highscore
import pl.pelotasplus.queens.domain.PositionState.BlockedBy
import pl.pelotasplus.queens.navigation.MainDestinations
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val avatarRepository: AvatarRepository,
    private val highscoreRepository: HighscoreRepository
) : ViewModel() {

    private val navArgs by lazy {
        savedStateHandle.toRoute<MainDestinations.GameScreen>()
    }

    private val _state = MutableStateFlow(
        State(
            boardState = GameBoardState(size = navArgs.boardSize),
            gameStartTime = System.currentTimeMillis(),
            gameStatus = State.GameStatus.InProgress
        )
    )
    val state = _state.asStateFlow()

    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleEvent(Event.LoadSelectedAvatar(navArgs.selectedAvatar))
        handleEvent(Event.MonitorGameFinish)
    }

    @Suppress("LongMethod")
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
                if (_state.value.gameStatus == State.GameStatus.Finished) {
                    return
                }

                _state.update { currentState ->
                    if (currentState.boardState.grid[event.position.row][event.position.col] is BlockedBy) {
                        viewModelScope.launch {
                            _effect.send(Effect.Vibrate)
                        }
                    }
                    currentState.copy(
                        boardState = currentState.boardState.handleClick(
                            event.position.row,
                            event.position.col
                        ),
                    )
                }
            }

            Event.OnPlayAgainClicked -> {
                _state.update {
                    it.copy(
                        boardState = it.boardState.emptyGrid(),
                        gameStatus = State.GameStatus.InProgress,
                        gameStartTime = System.currentTimeMillis()
                    )
                }
            }

            is Event.OnAnimationFinished -> {
                _state.update { currentState ->
                    currentState.copy(
                        boardState = currentState.boardState.shakeQueen(
                            listOf(event.position),
                            false
                        )
                    )
                }
            }

            Event.OnTrophyClicked -> {
                viewModelScope.launch {
                    _effect.send(Effect.ShowHighscores)
                }
            }

            Event.MonitorGameFinish -> {
                _state
                    .filter {
                        it.selectedAvatar != null && it.boardState.movesLeft <= 0
                                && it.gameStatus == State.GameStatus.InProgress
                    }
                    .distinctUntilChanged()
                    .flatMapLatest { state ->
                        check(state.selectedAvatar != null)
                        highscoreRepository.addHighscore(
                            Highscore(
                                avatar = state.selectedAvatar,
                                boardSize = state.boardState.size,
                                startTime = state.gameStartTime,
                                gameTime = (System.currentTimeMillis() - state.gameStartTime) / 1000
                            )
                        ).map {
                            state
                        }
                    }
                    .onEach { state ->
                        check(state.selectedAvatar != null)
                        _state.update {
                            it.copy(
                                gameStatus = State.GameStatus.Finished
                            )
                        }
                        @Suppress("MagicNumber")
                        _effect.send(
                            Effect.ShowFinished(
                                Effect.ShowFinished.WinnerDetails(
                                    state.selectedAvatar,
                                    (System.currentTimeMillis() - state.gameStartTime) / 1000
                                )
                            )
                        )
                    }
                    .catch {
                        // add error handling
                    }
                    .launchIn(viewModelScope)
            }
        }

    }

    data class State(
        val boardState: GameBoardState,
        val selectedAvatar: Avatar? = null,
        val gameStartTime: Long = 0L,
        val gameStatus: GameStatus = GameStatus.NotStarted
    ) {
        enum class GameStatus {
            NotStarted,
            InProgress,
            Finished
        }
    }

    sealed interface Effect {
        data object Vibrate : Effect
        data class ShowFinished(
            val winnerDetails: WinnerDetails,
        ) : Effect {
            data class WinnerDetails(
                val avatar: Avatar,
                val timeElapsed: Long
            )
        }

        data object ShowHighscores : Effect
    }

    sealed interface Event {
        data object MonitorGameFinish : Event
        data object OnPlayAgainClicked : Event
        data object OnTrophyClicked : Event
        data class LoadSelectedAvatar(val avatarId: Int) : Event
        data class OnTileClicked(val position: GridPosition) : Event
        data class OnAnimationFinished(val position: GridPosition) : Event
    }
}
