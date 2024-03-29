package daytime;

import mainwindow.Main;
import utils.JLogArea;
import utils.SocketUtils;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TimeServer {
    private final JLogArea taLogger;
    private TimeServerThread thread;

    public TimeServer(JLogArea taLogger) {
        this.taLogger = taLogger;
    }

    public boolean isRunning() {
        return thread != null && thread.isRunning();
    }

    public void start() {
        thread = new TimeServerThread(taLogger);
        thread.start();
    }

    public void stop() {
        thread.quit();
        taLogger.append("Daytime Server beendet.\n\n");
    }
}

class TimeServerThread extends Thread {
    private final JLogArea taLogger;
    private ServerSocket serverSocket;
    private boolean isRunning;

    public TimeServerThread(JLogArea taLogger) {
        this.taLogger = taLogger;
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(10013);
            taLogger.append("Daytime Server gestartet.\n" + "Verbinde Dich mit Telnet an " + SocketUtils.getHostIP() +
                    " und auf Port " + Main.PORT_TIME + "\nDu kannst die Ausgabe vergleichen mit z.B. time-a-g.nist" + ".gov auf Port 13.\n");
            isRunning = true;

            while (isRunning) {
                try (Socket client = serverSocket.accept()) {
                    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
                    String ausgabe = now + " (UTC)";
                    taLogger.append("Client verbunden, sende: " + ausgabe + "\n");
                    PrintWriter out = new PrintWriter(client.getOutputStream());
                    out.println(ausgabe);
                    out.flush();
                    out.close();
                } catch (SocketException e) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }
    }

    public void quit() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            taLogger.append("Fehler beim Schließen von Daytime-Server!\n");
            e.printStackTrace();
        }
        isRunning = false;
    }
}