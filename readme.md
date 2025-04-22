## N Queens


# Build instructions

To build the app, just open this project in Android Studio or build an APK using `./gradlew assemble`

# App in action

Here's video showing app in actions:

![](https://github.com/pelotasplus/nqueens/blob/6e785629b7caa9d433721ff4a9202ec68cef2089/app/src/main/res/drawable/queens.gif)

# App architecture

App is using:

 - Dagger Hilt for DI
 - Jetpack Compose w/ Compose Navigation for the UI layer
 - mockk and turbine for unit tests
 - maestro for UI tests
 - simple MVI-alike framework for ViewModels
 - Room for storing highscores

# Game mechanics

After selecting the board size, ie. 4x4, an in-memory representation is created -- see [GameBoardState].
This state is immutable, so any changes create a brand new instance.

The game board itself is represented by two dimensional array of [PositionState]s.
Tapping on board positions toggles values stored in the array and updates the UI accordingly.

Putting a queen on the board, ie. tapping on a position that is [PositionState.Empty] not
only flips that position to [PositionState.Queen] but also visits all other positions that are covered by the queen
and flips them to [PositionState.BlockedBy].
When users tap on a visibly empty position but covered by one or more queens, game can immediately react accordingly.

After every game a highscore entry is created and stored locally.
