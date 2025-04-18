package pl.pelotasplus.queens.features.game_screen

import androidx.lifecycle.SavedStateHandle
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
import pl.pelotasplus.queens.data.AvatarRepository
import pl.pelotasplus.queens.domain.model.Avatar
import pl.pelotasplus.queens.navigation.MainDestinations
import pl.pelotasplus.queens.ui.composable.GameBoardPosition
import pl.pelotasplus.queens.ui.composable.GameBoardPositionState
import pl.pelotasplus.queens.ui.composable.GameBoardPositionState.*
import pl.pelotasplus.queens.ui.composable.GameBoardState
import java.util.UUID
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class GameViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val avatarRepository: AvatarRepository
) : ViewModel() {

//    private val navArgs by lazy {
//        savedStateHandle.toRoute<MainDestinations.GameScreen>()
//    }

    val navArgs = MainDestinations.GameScreen(
        selectedAvatar = 1,
        boardSize = 4
    )

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

    private fun blockOthers(
        board: Array<Array<GameBoardPositionState>>,
        row: Int,
        col: Int,
        block: Boolean
    ) {
        val size = board[0].size

        println("XXX blockOthers $row $col $block")

        // visiting current row and col
        for (i in 0 until size) {
            if (block) {
                board[row][i] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
                board[i][col] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            } else {
                board[row][i] -= BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
                board[i][col] -= BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            }
        }

        // visiting top-left diagonal
        val delta = min(row, col)
        var dr = row - delta
        var dc = col - delta

        while (dr < size && dc < size) {
            if (block) {
                board[dr][dc] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            } else {
                board[dr][dc] -= BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            }
            dr += 1
            dc += 1
        }

        // visiting top-right diagonal
        val delta2 = min(row, size - col - 1)
        var dr2 = row - delta2
        var dc2 = col + delta2
        while (dr2 < size && dc2 >= 0) {
            if (block) {
                board[dr2][dc2] += BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            } else {
                board[dr2][dc2] -= BlockedBy(
                    row, col,
                    listOf(GameBoardPosition(row, col))
                )
            }
            dr2 += 1
            dc2 -= 1
        }
    }

    fun handleEvent(event: Event) {
        when (event) {
            is Event.LoadSelectedAvatar -> {
                println("XXX event ${event.avatarId}")
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
                val currentBoardState = _state.value.boardState

                val positionState = currentBoardState.grid[event.position.row][event.position.col]

                println("XXX clicked ${event.position.row} x ${event.position.col} -> $positionState")

                val newBoardState = when (positionState) {
                    is BlockedBy -> {
                        currentBoardState.copy(
                            grid = currentBoardState.grid.apply {
                                positionState.positions.forEach {
                                    this[it.row][it.col] = Queen(it.row, it.col, shake = true)
                                }

                            }
                        )
                    }

                    is Empty -> {
                        currentBoardState.copy(
                            grid = currentBoardState.grid.apply {
                                this[event.position.row][event.position.col] =
                                    Queen(event.position.row, event.position.col, false)
                                blockOthers(this, event.position.row, event.position.col, true)
                            }
                        )
                    }

                    is Queen -> {
                        currentBoardState.copy(
                            grid = currentBoardState.grid.apply {
                                this[event.position.row][event.position.col] =
                                    Empty(
                                        event.position.row,
                                        event.position.col
                                    )
                                blockOthers(this, event.position.row, event.position.col, false)
                            }
                        )
                    }
                }

                newBoardState.dump()

                _state.update {
                    it.copy(
                        boardState = newBoardState,
                        someLabel = UUID.randomUUID().toString()
                    )
                }
            }

            Event.OnRetryClicked -> {
                val size = _state.value.boardState.size
                _state.update {
                    it.copy(
                        boardState = it.boardState.copy(
                            grid = Array(size) { row ->
                                Array(size) { col ->
                                    Empty(row, col)
                                }
                            }
                        ),
                        gameStartTime = System.currentTimeMillis()
                    )
                }
            }

            is Event.OnAnimationFinished -> {
                println("XXX animation fishied for ${event.position.row} x ${event.position.col}")
                val newBoardState = _state.value.boardState

                newBoardState.dump()

                newBoardState.grid[event.position.row][event.position.col] =
                    Queen(event.position.row, event.position.col, false)

                newBoardState.dump()

                _state.update {
                    it.copy(
                        boardState = newBoardState,
                        someLabel = UUID.randomUUID().toString()
                    )
                }
            }
        }

    }

    data class State(
        val boardState: GameBoardState,
        val selectedAvatar: Avatar? = null,
        val gameStartTime: Long = 0L,
        val someLabel: String = "a"
    )

    sealed interface Effect {
    }

    sealed interface Event {
        data class LoadSelectedAvatar(val avatarId: Int) : Event
        data class OnTileClicked(val position: GameBoardPosition) : Event
        data class OnAnimationFinished(val position: GameBoardPosition) : Event
        data object OnRetryClicked : Event
    }
}
