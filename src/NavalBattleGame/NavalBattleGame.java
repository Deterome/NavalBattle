package NavalBattleGame;

import NavalBattleGame.GameEnums.GameEvent;
import NavalBattleGame.GameEnums.GameState;
import NavalBattleGame.GameRound.Round;
import NavalBattleGame.GameRound.RoundServerClient;
import NavalBattleGame.GameUsers.User;
import StateMachine.StateMachine;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class NavalBattleGame extends StateMachine<GameState, GameEvent> {

    public NavalBattleGame() {
        super(GameState.Intro);
        this.scheduleTimeoutEvent(GameEvent.IntroEnded, 3, TimeUnit.SECONDS);
    }

    @Override
    public void stopStateMachine() {
        this.stopScheduler();
    }

    @Override
    protected void onStateChange(GameState newState) {
        if (newState == GameState.Round) {
            createRound();
        } else if (newState == GameState.MainMenu && currentState == GameState.Round) {
            endRound();
        }
    }

    @Override
    protected void initTransitionTable() {
        addNewTransitionToTable(GameState.Intro, GameEvent.IntroEnded, GameState.SetPlayerName);

        addNewTransitionToTable(GameState.SetPlayerName, GameEvent.PlayerNameEntered, GameState.MainMenu);

        addNewTransitionToTable(GameState.MainMenu, GameEvent.RoundCreated, GameState.Round);
        addNewTransitionToTable(GameState.MainMenu, GameEvent.JoinMenuOpened, GameState.JoinToRoundMenu);
        addNewTransitionToTable(GameState.MainMenu, GameEvent.GameExited, GameState.Exit);

        addNewTransitionToTable(GameState.JoinToRoundMenu, GameEvent.BackToMenu, GameState.MainMenu);

        addNewTransitionToTable(GameState.Round, GameEvent.RoundEnded, GameState.MainMenu);
    }

    @Override
    protected void handleInvalidEvent(GameEvent event) {}

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void joinToRound(int roundPort) {
        try {
            connectionToRound = new RoundServerClient(this.user, new URI("ws://localhost:" + roundPort));
            connectionToRound.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void createRound() {
        this.currentRound = new Round(user);
    }

    private void endRound() {
        currentRound = null;
    }

    Round currentRound;
    RoundServerClient connectionToRound;

    User user;
}
