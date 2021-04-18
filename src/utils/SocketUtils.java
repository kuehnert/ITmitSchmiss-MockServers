package utils;

import java.net.*;

public class SocketUtils {
    public static String getHostIP() {
        // String ips = "";
        // Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        // for (NetworkInterface intface: networkInterfaces) {
        //     ips += intface.getInetAddresses();
        // }
        // return null;

        String ip = "<UNBEKANNT>"; try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002); ip = socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {}

        return ip;
    }

    public static String getSocketIP(Socket socket) {
        InetAddress ip = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress();
        if (ip instanceof Inet4Address) {
            return ip.toString().replaceFirst("/", "");
        } else {
            return ip.toString().replaceFirst("/", "");
        }
    }
}
