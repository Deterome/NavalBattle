package NavalBattleGame.GameRound;

import NavalBattleGame.GameUsers.Player;
import NavalBattleGame.GameUsers.PlayerAction;
import NavalBattleGame.GameUsers.User;
import NavalBattleGame.ToolsForGame.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class RoundServer extends WebSocketServer {

    public RoundServer(Round round, String host, int port) {
        super(new InetSocketAddress(host, port));

        this.round = round;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {}

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // обработка отключения пользователя
        round.disconnectUserFromRound(clients.get(conn));
        try {
            sendRoundInformationToClients();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // обрабатывать действия подключённых пользователей
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(message);

            switch (CommandToServer.getCommandByString(jsonNode.path("command").asText())) {
                case CreateUser -> {
                    createUser(conn, jsonNode.path("object").asText());
                }
                case PlayerIsReady -> {
                    setPlayerIsReady(jsonNode.path("object").asText());
                }
                case UpdatePlayerInformation -> {
                    updatePlayerInformation(jsonNode.path("object").asText());
                }
                case ProcessMove -> {
                    processClientMovement(jsonNode.path("object").asText());
                }
                case AddRoleToUser -> {
                    processAddingRoleToUser(conn, jsonNode.path("object").asText());
                }
                case DeleteUserRole -> {
                    processDeletingUserRole(conn, jsonNode.path("object").asText());
                }
            }

            sendRoundInformationToClients();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void processAddingRoleToUser(WebSocket conn, String jsonPackage) throws JsonProcessingException {
        var userWithRoleToAdd = JsonParser.makeUserNameAndRoleEntryFromJsonString(jsonPackage);
        var user = round.findUserByName(userWithRoleToAdd.getKey());
        round.giveUserRole(user, userWithRoleToAdd.getValue(), false);
        notifyClientsToAddRoleToUser(clients.get(conn), user, userWithRoleToAdd.getValue());
    }

    private void processDeletingUserRole(WebSocket conn, String jsonPackage) throws JsonProcessingException {
        var userWithRoleToDelete = JsonParser.makeUserNameAndRoleEntryFromJsonString(jsonPackage);
        var user = round.findUserByName(userWithRoleToDelete.getKey());
        round.deleteUserRole(user, userWithRoleToDelete.getValue(), false);
        notifyClientsToDeleteUserRole(clients.get(conn), user, userWithRoleToDelete.getValue());
    }

    public void notifyClientsAboutPlayerMovement(Player player, String movementInfo) throws JsonProcessingException {
        sendJsonPackageOfPlayerToClients(
                player,
                JsonParser.createJsonPackageForClient(CommandToClient.ProcessPlayerMove, movementInfo)
        );
    }

    public void processClientMovement(String movementInfo) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(movementInfo);
        var playerAction = PlayerAction.getActionByString(jsonNode.path(PlayerAction.enumString).asText());
        var coordinates = JsonParser.makeCoordinatesFromJsonString(jsonNode.path("coordinates").asText());
        var player = round.findPlayerByNickname(jsonNode.path("player_nickname").asText());
        round.makeAction(player,
                playerAction,
                coordinates.getKey(),
                coordinates.getValue()
                );

        notifyClientsAboutPlayerMovement(player, movementInfo);
    }

    private void updatePlayerInformation(String playerJsonStr) throws JsonProcessingException {
        var player = JsonParser.makePlayerFromJsonString(playerJsonStr);
        round.updatePlayerInfo(player);

        sendPlayerInformationToClients(player);
    }

    private void sendPlayerInformationToClients(Player player) throws JsonProcessingException {
        sendJsonPackageOfPlayerToClients(
                player,
                JsonParser.createJsonPackageForClient(CommandToClient.UpdatePlayerInformation,
                        JsonParser.createJsonStringFromPlayer(player))
        );
    }

    private void sendJsonPackageOfPlayerToClients(Player player, String jsonPackageStr) {
        for (var clientsEntry: clients.entrySet()) {
            if (round.findPlayerByUser(clientsEntry.getValue()) == null ||
                    !round.findPlayerByUser(clientsEntry.getValue()).getNickname().equals(player.getNickname())) {
                if (clientsEntry.getKey().isOpen()) {
                    clientsEntry.getKey().send(jsonPackageStr);
                }
            }
        }
    }

    public void sendPlayersInformationToClients() throws JsonProcessingException {
        for (var player: round.players.values()) {
            sendPlayerInformationToClients(player);
        }
    }

    public void sendJsonPackageOfUserToClients(User user, String jsonPackageStr) {
        for (var clientsEntry: clients.entrySet()) {
            if (!clientsEntry.getValue().getName().equals(user.getName())) {
                if (clientsEntry.getKey().isOpen()) {
                    clientsEntry.getKey().send(jsonPackageStr);
                }
            }
        }
    }

    public void notifyClientsToAddRoleToUser(User userInvoker, User user, UserRole userRole) {
        try {
            sendJsonPackageOfUserToClients(
                    userInvoker,
                    JsonParser.createJsonPackageForServer(
                            CommandToServer.AddRoleToUser,
                            JsonParser.createJsonStringOfUserWithRole(user.getName(), userRole)
                    )
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyClientsToDeleteUserRole(User userInvoker,User user, UserRole userRole) {
        try {
            sendJsonPackageOfUserToClients(
                    userInvoker,
                    JsonParser.createJsonPackageForServer(
                            CommandToServer.DeleteUserRole,
                            JsonParser.createJsonStringOfUserWithRole(user.getName(), userRole)
                    )
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestPlayersInformationFromClients() {
        try {
            sendPackageToClients(
                    JsonParser.createJsonPackageForClient(CommandToClient.SendPlayerInformationToServer, "")
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    void notifyPlayersToStartMatch() {
        try {
            sendPackageToClients(JsonParser.createJsonPackageForClient(CommandToClient.StartMatch, ""));
            sendPlayersInformationToClients();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    void setPlayerIsReady(String playerJsonStr) {
        Player player = round.findPlayerByNickname(playerJsonStr);

        if (player != null) round.setPlayerReadiness(player, true);
    }

    void createUser(WebSocket conn, String userJsonStr) throws JsonProcessingException {
        var newUser = JsonParser.makeUserFromJsonString(userJsonStr);

        clients.put(conn, newUser);
        round.connectUserToRound(newUser);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {}

    private void sendRoundInformationToClients() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();

        var joinedUsers = round.getJoinedUsers();
        for (var entry: joinedUsers.entrySet()) {
            String user = objectMapper.writeValueAsString(entry.getKey());
            String userRoles = objectMapper.writeValueAsString(entry.getValue());

            jsonObject.put(user, userRoles);
        }

        sendPackageToClients(
                JsonParser.createJsonPackageForClient(CommandToClient.SetUsersList, objectMapper.writeValueAsString(jsonObject))
        );
    }

    public void notifyClientsToStopWaitingPlayers() {
        try {
            sendPackageToClients(JsonParser.createJsonPackageForClient(CommandToClient.StopWaitingForPlayers, ""));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPackageToClients(String packageStr) {
        for (var clientConn: clients.keySet()) {
            if (!clientConn.isClosing() && !clientConn.isClosed()) {
                clientConn.send(packageStr);
            }
        }
    }

    private HashMap<WebSocket, User> clients = new HashMap<>();

    private Round round;
}
