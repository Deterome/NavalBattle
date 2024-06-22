package NavalBattleGame.GameRound;

import NavalBattleGame.GameElements.SeaField;
import NavalBattleGame.GameElements.Ship;
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
    }

    public boolean isLanOpened() {
        return roundServer != null;
    }

    public void openLAN() {
        if (roundServer == null) {
            roundServer = new RoundServer(this);
            roundServer.start();
        }
    }

    public void closeLAN() {
        if (roundServer != null){
            try {
                roundServer.stop();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.roundServer = null;
        }
    }

    public int getRoundServerPort() {
        if (roundServer == null) {
            return -1;
        }
        return roundServer.getPort();
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
                players.put(userSet.getKey(), new Player());
            }
        }

        for (var player: players.entrySet()) {
            giveDefaultShipsToPlayer(player.getValue());
            giveDefaultFieldToPlayer(player.getValue());
        }
    }

    private void giveDefaultShipsToPlayer(Player player) {
        player.addShips(new Ship(6), 1);
        player.addShips(new Ship(5), 2);
        player.addShips(new Ship(4), 3);
        player.addShips(new Ship(3), 4);
        player.addShips(new Ship(2), 5);
        player.addShips(new Ship(1), 6);
    }

    private void giveDefaultFieldToPlayer(Player player) {
        player.setField(new SeaField(16, 16));
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

    public Player getPlayerByUser(User user) {
        return players.get(user);
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

    public boolean hasUserRole(User user, UserRole role) {
        if (joinedUsers != null) {
            if (joinedUsers.get(user) == null) {
                return false;
            } else {
                return joinedUsers.get(user).contains(role);
            }
        }
        return false;
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

    public void setJoinedUsers(HashMap<User, ArrayList<UserRole>> joinedUsers) {
        this.joinedUsers = joinedUsers;
    }

    HashMap<User, ArrayList<UserRole>> joinedUsers =new HashMap<>();
    HashMap<User, Player> players = new HashMap<>();
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
                closeLAN();
                game.processEvent(GameEvent.RoundEnded);
            }
            case PlacementOfShips -> {
                if (roundServer != null) {
                    roundServer.notifyToStopWaitingPlayers();
                }
                createPlayers();
            }
        }
    }

    RoundServer roundServer;
    NavalBattleGame game;

}
