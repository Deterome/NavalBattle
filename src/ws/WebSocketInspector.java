package ws;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

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

    public static String findHost() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) { // Проверяем, что интерфейс активен и не является петлевым
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (!address.isLoopbackAddress() && address.getAddress().length == 4) { // Проверяем, что адрес не петлевой и IPv4
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Ошибка получения IP-адреса: " + e.getMessage());
        }

        return "localhost";
    }

}
