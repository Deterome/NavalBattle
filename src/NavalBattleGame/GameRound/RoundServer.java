package NavalBattleGame.GameRound;

import NavalBattleGame.GameUsers.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import ws.WebSocketInspector;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class RoundServer extends WebSocketServer {

    public RoundServer(Round round) {
        super( new InetSocketAddress("localhost", WebSocketInspector.findFreePortOnHost("localhost",8000, 8500)));

        this.round = round;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // обработка отключенпя пользователя
        round.disconnectUserFromRound(clients.get(conn));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // обрабатывать действия подключённых пользователей
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(message);

            switch (CommandToServer.CommandByString(jsonNode.path("command").asText())) {
                case CreateUser -> {
                    User newUser = objectMapper.readValue(jsonNode.path("object").asText(), User.class);
                    clients.put(conn, newUser);
                    round.connectUserToRound(newUser);
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        // скинуть порт, на котором был запущен сервер
    }

    private HashMap<WebSocket, User> clients = new HashMap<>();

    private Round round;
}
