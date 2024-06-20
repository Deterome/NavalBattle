package NavalBattleGame.GameRound;

import NavalBattleGame.GameUsers.Player;
import NavalBattleGame.GameUsers.User;
import StateMachine.StateMachine;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class Round extends StateMachine<RoundStates, RoundEvents> {

    public Round(User creator) {
        super(RoundStates.WaitingForPlayers);

        connectUserToRound(creator);
        giveUserRole(creator, UserRole.Admin);

        roundServer = new RoundServer(this);
        roundServer.start();
    }

    public int getPortOfRound() {
        return this.roundServer.getPort();
    }

    void connectUserToRound(User user) {
        joinedUsers.put(user, new ArrayList<>());

        if (!tryAddPlayer(user)) {
            tryAddWatcher(user);
        }
    }

    boolean tryAddPlayer(User user) {
        if (players.size() < maxCountOfPlayers && currentState.equals(RoundStates.WaitingForPlayers)) {
            var userRoles = joinedUsers.get(user);
            if (userRoles.contains(UserRole.Watcher)) {
                deleteUserRole(user, UserRole.Watcher);
            }
            userRoles.add(UserRole.Player);

            players.add(new Player(user));
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

    void deleteUserRole(User user, UserRole roleToDelete) {
        var userRoles = joinedUsers.get(user);

        userRoles.remove(roleToDelete);

        if (userRoles.isEmpty()) {
            tryAddWatcher(user);
        }
    }

    void giveUserRole(User user, UserRole newRole) {
        switch (newRole){
            case Admin -> tryAddAdmin(user);
            case Player -> tryAddPlayer(user);
            case Watcher -> tryAddWatcher(user);
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

    }

    RoundServer roundServer;
}
