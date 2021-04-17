package echo;

import mainwindow.Main;
import utils.SocketUtils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class EchoServer {
    private final JTextArea taLogger;
    private EchoServerThread thread;

    public EchoServer(JTextArea taLogger) {
        this.taLogger = taLogger;
    }

    public boolean isRunning() {
        return thread != null && thread.isRunning();
    }

    public void start() {
        thread = new EchoServerThread(taLogger);
        thread.start();
    }

    public void stop() {
        thread.quit(); taLogger.append("Echo-Server gestoppt.\n\n");
    }
}

class EchoServerThread extends Thread {
    private final JTextArea taLogger;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private final ArrayList<EchoClientThread> clients = new ArrayList<EchoClientThread>();
    private int clientCount = 0;

    public EchoServerThread(JTextArea taLogger) {
        this.taLogger = taLogger; isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Main.PORT_ECHO);
            taLogger.append("Echo-Server gestartet.\n" + "Verbinde Dich mit Telnet an " + SocketUtils.getHostIP() +
                    " und auf Port " + Main.PORT_ECHO + ".\n");
            isRunning = true;

            while (isRunning) {
                Socket client; try {
                    client = serverSocket.accept();
                } catch (SocketException e) {
                    System.err.println("FEHLER"); isRunning = false; break;
                }

                EchoClientThread clientThread = new EchoClientThread(this, client, ++clientCount);
                clients.add(clientThread); clientThread.start();
            }
        } catch (IOException e) {
            taLogger.append("SocketException\n"); e.printStackTrace(); isRunning = false;
            for (EchoClientThread clientThread : clients) {
                clientThread.kill();
            }
        }
    }

    public JTextArea getLogger() {
        return taLogger;
    }

    public void quit() {
        try {
            for (EchoClientThread client : clients) {
                client.kill();
            } serverSocket.close();
        } catch (IOException e) {
            taLogger.append("Fehler beim Schließen vom Echo-Server!\n"); e.printStackTrace();
        } isRunning = false;
    }

    public void removeClient(EchoClientThread thread) {
        clients.remove(thread); taLogger.append("Verbindung zu Client " + thread + " beendet.\n");
    }
}

class EchoClientThread extends Thread {
    private static final String WELCOME = "Willkommen beim Echo-Server. Gebe \"quit\" ein, um die Verbindung zu " +
            "schliessen.\n";
    private final Socket socket;
    private final EchoServerThread server;
    private JTextArea taLogger;
    private BufferedReader in;
    private PrintWriter out;
    private boolean isRunning;
    private final int number;
    private final String address;

    public EchoClientThread(EchoServerThread server, Socket socket, int number) {
        this.server = server; this.socket = socket; this.number = number;
        this.address = SocketUtils.getSocketIP(socket);

        server.getLogger().append("Neuer Client verbunden: " + getClientName() + "\n"); try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); out.println(WELCOME);
        } catch (java.io.IOException e) {
            server.getLogger().append("FEHLER: IOException in EchoClientThread\n"); e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String quitRegex = "^(q|x|quit|exit|bye)$";

        isRunning = true;

        while (isRunning) {
            try {
                String incoming = in.readLine(); if (incoming == null) {
                    isRunning = false; break;
                } else if (incoming.matches(quitRegex)) {
                    out.println("Bye-bye!"); isRunning = false; break;
                } server.getLogger().append(number + ": " + incoming + "\n"); out.println(incoming);
            } catch (IOException e) {
                server.getLogger().append("FEHLER: \n"); e.printStackTrace(); isRunning = false;
            }
        }

        server.getLogger().append("Schliesse Client " + getClientName() + "\n");

        try {
            in.close(); out.close(); socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        isRunning = false;
    }

    public String getClientName() {
        return String.format("%d (%s)", number, address);
    }
}
