package NavalBattleGame.GameRound;

import NavalBattleGame.GameElements.SeaField;
import NavalBattleGame.GameElements.Ship;
import NavalBattleGame.GameEnums.GameEvent;
import NavalBattleGame.GameUsers.*;
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

    public String getRoundServerAddress() {
        return roundServer.getAddress().toString();
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

    public ArrayList<Player> getPlayersList() {
        return new ArrayList<>(this.players.values());
    }

    public void makeAction(Player player, PlayerActions playerActions, String jsonStringWithCommand) {
        if (isPlayerActing(player)) {
            switch (playerActions) {
                case Attack -> {
                    var attackCoords = CoordinatesParser.getCoordinatesByJson(jsonStringWithCommand);
                    if (getNextPlayerToAct().getField().getSeaTable().get(attackCoords.getKey()) != null &&
                            getNextPlayerToAct().getField().getSeaTable().get(attackCoords.getKey()).get(attackCoords.getValue()) != null &&
                            !getNextPlayerToAct().getField().getSeaTable().get(attackCoords.getKey()).get(attackCoords.getValue()).isShelled()
                    ) {
                        player.attackPlayer(getNextPlayerToAct(), attackCoords.getKey(), attackCoords.getValue());
                        if (!getNextPlayerToAct().getField().getSeaTable().get(attackCoords.getKey()).get(attackCoords.getValue()).hasShip()) {
                            switchAttackingPlayer();
                        } else {
                            if (getActingPlayer() instanceof Bot) {
                                ((Bot)getActingPlayer()).attack();
                            }
                        }
                    }

                    for (var checkingPlayer: players.values()) {
                        if (didPlayerLose(checkingPlayer)) {
                            processEvent(RoundEvents.MatchEnd);
                        }
                    }
                }
            }
        }
    }

    private boolean didPlayerLose(Player player) {
        return player.getCountOfRemainingShips() == 0;
    }

    public Player getNextPlayerToAct() {
        return (Player) players.values().toArray()[(actingPlayerId + 1)%players.size()];
    }

    public Player getActingPlayer() {
        return (Player) players.values().toArray()[actingPlayerId];
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
                players.put(userSet.getKey().getName(), new Player(userSet.getKey().getName()));
            }
        }
        int botId = 1;
        while (players.size() < minCountOfPlayers) {
            String botName = "Bot" + botId++;
            Bot newBot = new Bot(botName, this);
            players.put(botName, newBot);
            bots.add(newBot);
        }

        for (var player: players.values()) {
            playersReadiness.put(player, false);
        }
    }

    private void onPlacementOfShipsStarted() {
        createPlayers();

        for (var player: players.entrySet()) {
            giveDefaultShipsToPlayer(player.getValue());
            giveDefaultFieldToPlayer(player.getValue());
        }
        for (var bot: bots) {
            bot.placeShipsOnField();
            setPlayerReadiness(bot, true);
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
    }

    public Player getPlayerByUser(User user) {
        return players.get(user.getName());
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

    public Player getPlayerByNickname(String nickName) {
        for (var player: players.values()) {
            if (player.getNickname().equals(nickName)) return player;
        }
        return null;
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

    private HashMap<Player, Boolean> playersReadiness = new HashMap<>();

    public void setPlayerReadiness(Player player, boolean readiness) {
        playersReadiness.put(player, readiness);

        for (var playerReadiness: playersReadiness.entrySet()) {
            if (!playerReadiness.getValue()) return;
        }
        processEvent(RoundEvents.StartMatch);
    }

    public boolean isPlayerReady(Player player) {
        return playersReadiness.get(player);
    }

    private void onMatchStart() {
        if (isLanOpened()) {
            roundServer.notifyPlayersToStartMatch();
        }
    }

    private void switchAttackingPlayer() {
        actingPlayerId = (actingPlayerId + 1)%players.size();

        if ((Player)players.values().toArray()[actingPlayerId] instanceof Bot) {
            ((Bot)players.values().toArray()[actingPlayerId]).attack();
        }
    }

    public boolean isPlayerActing(Player player) {
        return (Player)players.values().toArray()[actingPlayerId] == player;
    }

    HashMap<User, ArrayList<UserRole>> joinedUsers =new HashMap<>();
    HashMap<String, Player> players = new HashMap<>();
    ArrayList<Bot> bots = new ArrayList<>();
    int actingPlayerId = 0;
    int maxCountOfPlayers = 2;
    int minCountOfPlayers = 2;

    @Override
    public void stopStateMachine() {
        for (var bot: bots) {
            bot.stopScheduler();
        }
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
                    roundServer.notifyClientsToStopWaitingPlayers();
                }
                onPlacementOfShipsStarted();
            }
            case Match -> {
                onMatchStart();
            }
        }
    }

    RoundServer roundServer;
    NavalBattleGame game;

}
