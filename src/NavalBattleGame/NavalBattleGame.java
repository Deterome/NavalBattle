package NavalBattleGame;

import NavalBattleGame.GameEnums.GameEvent;
import NavalBattleGame.GameEnums.GameState;
import StateMachine.StateMachine;

import java.util.concurrent.TimeUnit;

public class NavalBattleGame extends StateMachine<GameState, GameEvent> {

    public NavalBattleGame() {
        super(GameState.Intro);
        this.scheduleTimeoutEvent(GameEvent.IntroEnded, 2, TimeUnit.SECONDS);
    }

    @Override
    public void StopStateMachine() {
        this.stopScheduler();
    }

    @Override
    protected void onStateChange(GameState newState) {
    }

    @Override
    protected void initTransitionTable() {
        addNewTransitionToTable(GameState.Intro, GameEvent.IntroEnded, GameState.MainMenu);

        addNewTransitionToTable(GameState.MainMenu, GameEvent.SingleplayerGameStarted, GameState.Game);
        addNewTransitionToTable(GameState.MainMenu, GameEvent.MultiplayerGameStarted, GameState.Game);
        addNewTransitionToTable(GameState.MainMenu, GameEvent.GameExited, GameState.Exit);

        addNewTransitionToTable(GameState.Game, GameEvent.GamePaused, GameState.Pause);
        addNewTransitionToTable(GameState.Game, GameEvent.GameEnded, GameState.GameOver);

        addNewTransitionToTable(GameState.Pause, GameEvent.SingleplayerGameStarted, GameState.Game);
    }

    @Override
    protected void handleInvalidEvent(GameEvent event) {
    }
}
