## N Queens


# Build instructions

To build the app, just open this project in Android Studio or build an APK using `./gradlew assemble`

# App in action

Here's video showing app in actions:

<img src="https://github.com/pelotasplus/nqueens/blob/6e785629b7caa9d433721ff4a9202ec68cef2089/app/src/main/res/drawable/queens.gif" width="30%" height="auto">

# App architecture

App is using:

 - Dagger Hilt for DI
 - Jetpack Compose w/ Compose Navigation for the UI layer
 - mockk and turbine for unit tests
 - maestro for UI tests
 - simple MVI-alike framework for ViewModels
 - Room for storing highscores

# Game mechanics

After selecting the board size, ie. 4x4, an in-memory representation is created -- see [GameBoardState](https://github.com/pelotasplus/nqueens/blob/58b5e0016dd1ae652b162a8998e11ec08964b677/app/src/main/java/pl/pelotasplus/queens/domain/GameBoardState.kt#L6).
This state is immutable, so any changes create a brand new instance.

The game board itself is represented by two dimensional array of [PositionStates](https://github.com/pelotasplus/nqueens/blob/58b5e0016dd1ae652b162a8998e11ec08964b677/app/src/main/java/pl/pelotasplus/queens/domain/PositionState.kt#L4).
Tapping on board positions toggles values stored in the array and updates the UI accordingly.

Putting a queen on the board, ie. tapping on a position that is [PositionState.Empty](https://github.com/pelotasplus/nqueens/blob/58b5e0016dd1ae652b162a8998e11ec08964b677/app/src/main/java/pl/pelotasplus/queens/domain/PositionState.kt#L4) not
only flips that position to [PositionState.Queen](https://github.com/pelotasplus/nqueens/blob/58b5e0016dd1ae652b162a8998e11ec08964b677/app/src/main/java/pl/pelotasplus/queens/domain/PositionState.kt#L6) but also visits all other positions that are covered by the queen
and flips them to [PositionState.BlockedBy](https://github.com/pelotasplus/nqueens/blob/58b5e0016dd1ae652b162a8998e11ec08964b677/app/src/main/java/pl/pelotasplus/queens/domain/PositionState.kt#L10).
See [handleClick](https://github.com/pelotasplus/nqueens/blob/58b5e0016dd1ae652b162a8998e11ec08964b677/app/src/main/java/pl/pelotasplus/queens/domain/GameBoardState.kt#L20) and [blockOthers](https://github.com/pelotasplus/nqueens/blob/58b5e0016dd1ae652b162a8998e11ec08964b677/app/src/main/java/pl/pelotasplus/queens/domain/GameBoardState.kt#L77) for exact algorithm.

When users tap on a visibly empty position but covered by one or more queens, game can immediately react accordingly.

After every game a highscore entry is created and stored locally in on-device SQL database.
