package NavalBattleGame.GameRound;

import NavalBattleGame.GameUsers.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class RoundServerClient extends WebSocketClient {

    public RoundServerClient(User user, URI serverUri) {
        super(serverUri);

        this.user = user;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        ObjectMapper objectMapper = new ObjectMapper();

        String userJsonStr = "";

        try {
            userJsonStr = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ObjectNode jsonObject = objectMapper.createObjectNode();;
        jsonObject.put("command", CommandToServer.CreateUser.getCommandString());
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

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Произошла ошибка: " + ex);
    }

    User user;
}
