package NavalBattleGame.GameRound;

import NavalBattleGame.GameEnums.GameEvent;
import NavalBattleGame.GameUsers.Player;
import NavalBattleGame.GameUsers.User;
import NavalBattleGame.NavalBattleGame;
import StateMachine.StateMachine;

import java.util.ArrayList;
import java.util.HashMap;

public class Round extends StateMachine<RoundStates, RoundEvents> {

    public Round(NavalBattleGame game) {
        super(RoundStates.WaitingForPlayers);

        this.game = game;

        connectUserToRound(game.getUser());
        giveUserRole(game.getUser(), UserRole.Admin);

    }

    public void setRoundPort(int roundPort) {
        this.roundPort = roundPort;
    }

    public int getRoundPort() {
        return this.roundPort;
    }

    void connectUserToRound(User user) {
        joinedUsers.put(user, new ArrayList<>());

        if (!tryAddPlayer(user)) {
            tryAddWatcher(user);
        }
    }

    boolean tryAddPlayer(User user) {
        if (getCountOfUsersWithRole(UserRole.Player) < maxCountOfPlayers) {
            var userRoles = joinedUsers.get(user);
            if (userRoles.contains(UserRole.Watcher)) {
                deleteUserRole(user, UserRole.Watcher);
            }
            userRoles.add(UserRole.Player);
            return true;
        } else {
            return false;
        }
    }

    private boolean tryAddAdmin(User user) {
        joinedUsers.get(user).add(UserRole.Admin);
        return true;
    }

    private boolean tryAddWatcher(User user) {
        var userRoles = joinedUsers.get(user);
        if (userRoles.contains(UserRole.Player)) {
            deleteUserRole(user, UserRole.Player);
        }
        userRoles.add(UserRole.Watcher);

        return true;
    }

    private void createPlayers() {
        for (var userSet: joinedUsers.entrySet()) {
            if (userSet.getValue().contains(UserRole.Player)) {
                players.add(new Player(userSet.getKey()));
            }
        }
    }

    public void deleteUserRole(User user, UserRole roleToDelete) {
        var userRoles = joinedUsers.get(user);

        userRoles.remove(roleToDelete);

        if (userRoles.isEmpty()) {
            tryAddWatcher(user);
        }

        switch (roleToDelete){
            case Player -> players.remove(user);
        }
    }

    public int getCountOfUsersWithRole(UserRole role) {
        int countOfUsers = 0;

        for (var userSet: joinedUsers.entrySet()) {
            if (userSet.getValue().contains(role)) {
                countOfUsers++;
            }
        }

        return countOfUsers;
    }

    public void giveUserRole(User user, UserRole newRole) {
        if (!joinedUsers.get(user).contains(newRole)) {
            switch (newRole){
                case Admin -> tryAddAdmin(user);
                case Player -> tryAddPlayer(user);
                case Watcher -> tryAddWatcher(user);
            }
        }
    }

    void disconnectUserFromRound(User user) {
        joinedUsers.remove(user);
    }

    public HashMap<User, ArrayList<UserRole>> getJoinedUsers() {
        return joinedUsers;
    }

    HashMap<User, ArrayList<UserRole>> joinedUsers =new HashMap<>();
    ArrayList<Player> players = new ArrayList<>();
    int maxCountOfPlayers = 2;

    @Override
    public void stopStateMachine() {
        stopScheduler();
    }

    @Override
    protected void initTransitionTable() {

        addNewTransitionToTable(RoundStates.WaitingForPlayers, RoundEvents.StopWaitingForPlayers, RoundStates.PlacementOfShips);
        addNewTransitionToTable(RoundStates.WaitingForPlayers, RoundEvents.MatchEnd, RoundStates.MatchEnded);

        addNewTransitionToTable(RoundStates.PlacementOfShips, RoundEvents.StartMatch, RoundStates.Match);

        addNewTransitionToTable(RoundStates.Match, RoundEvents.PauseMatch, RoundStates.Pause);
        addNewTransitionToTable(RoundStates.Match, RoundEvents.MatchEnd, RoundStates.MatchEnded);

        addNewTransitionToTable(RoundStates.Pause, RoundEvents.ContinueMatch, RoundStates.Match);

    }

    @Override
    protected void handleInvalidEvent(RoundEvents event) {

    }

    @Override
    protected void onStateChange(RoundStates newState) {
        switch (newState) {
            case MatchEnded -> {
                game.processEvent(GameEvent.RoundEnded);
            }
        }
    }

    NavalBattleGame game;

    int roundPort;

}
