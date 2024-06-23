package NavalBattleGame.GameRound;

import NavalBattleGame.GameEnums.GameEvent;
import NavalBattleGame.GameUsers.User;
import NavalBattleGame.NavalBattleGame;
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
        ObjectMapper objectMapper = new ObjectMapper();

        String userJsonStr = "";

        try {
            userJsonStr = objectMapper.writeValueAsString(game.getUser());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ObjectNode jsonObject = objectMapper.createObjectNode();;
        jsonObject.put("command", CommandToServer.CreateUser.getStringOfCommand());
        jsonObject.put("object", userJsonStr);

        String jsonStr = "";

        try {
            jsonStr = objectMapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        send(jsonStr);
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
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyServerOfReadiness() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonPackage = objectMapper.createObjectNode();
        jsonPackage.put("command", CommandToServer.PlayerIsReady.getStringOfCommand());
        jsonPackage.put("object", game.getCurrentRound().getPlayerByUser(game.getUser()).getNickname());
        String jsonPackageStr = "";
        try {
            jsonPackageStr = objectMapper.writeValueAsString(jsonPackage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        send(jsonPackageStr);
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
