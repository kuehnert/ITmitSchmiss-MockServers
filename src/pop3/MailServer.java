package pop3;

import mainwindow.Main;
import utils.SocketUtils;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MailServer extends Thread {
    public static final String LOCAL_DOMAIN = "meinewelt.de";
    private final DefaultListModel<User> users;
    private final JTextArea taLog;
    JPPOP3Server window;
    private ServerSocket serverSocket;
    private boolean running;
    private List<MailClient> clients;

    public MailServer(JPPOP3Server window) {
        this.window = window;
        this.taLog = window.getLog();
        users = new DefaultListModel<>();
        clients = new ArrayList<>();
        loadStaticData();
    }

    private static String localize(String prefix) {
        return prefix + "@" + LOCAL_DOMAIN;
    }

    public JTextArea getTALog() {
        return taLog;
    }

    public boolean isRunning() {
        return running;
    }

    public User findUserByName(String name) {
        for (int i = 0; i < users.getSize(); i++) {
            User user = users.elementAt(i);

            if (user.getName().equals(name)) {
                return user;
            }
        }

        return null;
    }

    public DefaultListModel<User> getUsers() {
        return users;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(Main.PORT_POP3);
        } catch (IOException e) {
            taLog.append("FEHLER beim Starten des MailServers\n");
            e.printStackTrace();
            return;
        }

        running = true;
        taLog.append("Mail-Server gestartet auf " + SocketUtils.getHostIP() + " und Port " + Main.PORT_POP3 + "\n");
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Hallo");
                MailClient client = new MailClient(this, clientSocket);
                clients.add(client);
                client.start();
            } catch (SocketException e) {
                taLog.append("MailServer ist beendet.\n\n");
            } catch (IOException e) {
                taLog.append("FEHLER im MailServer\n");
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {
        running = false;
        for (MailClient c : clients) {
            c.stopThread();
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(MailClient deadClient) {
        if (clients.remove(deadClient)) {
            taLog.append(String.format("Client %s entfernt", deadClient.getName()));
        } else {
            taLog.append(String.format("FEHLER: Konnte Client %s nicht entfernen!", deadClient.getName()));
        }
    }

    private void loadStaticData() {
        User anna = new User("anna", "geheim");
        anna.getMails().add(0, new Mail("karl@heinz.de", localize("anna"), "Wichtig!", "Hallo Anna", new Date()));
        anna.getMails().add(1, new Mail("willi@beispiel.de", localize("anna"), "Ich liebe Dich!",
                "Hallo Anna,\nvon " + "mir auch!\nViele Gruesse,\nWilli", new Date()));
        users.add(0, anna);

        User bob = new User("bob", "topSecret");
        bob.getMails().add(0, new Mail("sokrates", localize("bob"), "Spam!", "Hi!", new Date()));
        users.add(1, bob);
    }
}
