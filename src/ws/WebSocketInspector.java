package ws;

import java.net.Socket;

public class WebSocketInspector {

    public static boolean isWebSocketServerRunning(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true; // Сервер запущен
        } catch (Exception e) {
            return false; // Сервер не запущен
        }
    }

    public static int findFreePortOnHost(String host, int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port++) {
            if (!isWebSocketServerRunning(host, port)) {
                return port;
            }
        }
        return -1;
    }

}
