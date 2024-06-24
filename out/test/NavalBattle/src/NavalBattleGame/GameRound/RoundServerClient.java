package NavalBattleGame.GameRound;

import NavalBattleGame.GameEnums.GameEvent;
import NavalBattleGame.GameUsers.Player;
import NavalBattleGame.GameUsers.User;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGame.ToolsForGame.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class RoundServerClient extends WebSocketClient {

    public RoundServerClient(NavalBattleGame game, URI serverUri) {
        super(serverUri);

        this.game = game;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        try {
            sendUserInformationToServer(game.getUser());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendUserInformationToServer(User user) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();;
        jsonObject.put("command", CommandToServer.CreateUser.getStringOfCommand());
        jsonObject.put("object", JsonParser.createJsonStringFromUser(user));

        send(objectMapper.writeValueAsString(jsonObject));

    }

    @Override
    public void onMessage(String message) {
        // обрабатывать посылаемые пакеты
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(message);

            switch (CommandToClient.getCommandByString(jsonNode.path("command").asText())) {
                case SetUsersList -> {
                    processSettingUserList(jsonNode.path("object").asText());
                }
                case StopWaitingForPlayers -> {
                    this.game.getCurrentRound().processEvent(RoundEvents.StopWaitingForPlayers);
                }
                case StartMatch -> {
                    this.game.getCurrentRound().processEvent(RoundEvents.StartMatch);
                }
                case SendPlayerInformationToServer -> {
                    sendPlayerInformationToServer();
                }
                case UpdatePlayerInformation -> {
                    updatePlayerInformation(jsonNode.path("object").asText());
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPlayerInformationToServer() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonPackage = objectMapper.createObjectNode();
        jsonPackage.put("command", CommandToServer.UpdatePlayerInformation.getStringOfCommand());
        jsonPackage.put("object", JsonParser.createJsonStringFromPlayer(game.getCurrentRound().findPlayerByUser(game.getUser())));

        send(objectMapper.writeValueAsString(jsonPackage));
    }

    private void updatePlayerInformation(String playerJsonStr) throws JsonProcessingException {
        game.getCurrentRound().updatePlayerInfo(JsonParser.makePlayerFromJsonString(playerJsonStr));
    }

    public void notifyServerOfReadiness() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonPackage = objectMapper.createObjectNode();
        jsonPackage.put("command", CommandToServer.PlayerIsReady.getStringOfCommand());
        jsonPackage.put("object", game.getCurrentRound().findPlayerByUser(game.getUser()).getNickname());

        try {
            send(objectMapper.writeValueAsString(jsonPackage));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void processSettingUserList(String jsonObjectStr) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> usersMapStr = objectMapper.readValue(jsonObjectStr, HashMap.class);
        HashMap<User, ArrayList<UserRole>> usersMap = new HashMap<>();
        for (var entry: usersMapStr.entrySet()) {
            User user = objectMapper.readValue(entry.getKey(), User.class);
            ArrayList<UserRole> roles = new ArrayList<>();
            ArrayList<String> rolesStrings =  objectMapper.readValue(entry.getValue(), ArrayList.class);
            for (var role: rolesStrings) {
                roles.add(UserRole.getCommandByString(role));
            }
            usersMap.put(user, roles);
        }

        game.getCurrentRound().setJoinedUsers(usersMap);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        game.processEvent(GameEvent.RoundEnded);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Произошла ошибка: " + ex);
    }

    NavalBattleGame game;
}
