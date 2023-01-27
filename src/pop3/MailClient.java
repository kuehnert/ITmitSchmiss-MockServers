package pop3;

import utils.SocketUtils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tries to implement a POP3-Server based on https://tools.ietf.org/html/rfc1081
 */
class MailClient extends Thread {
    public static final int STATE_CONNECTED = 0;
    public static final int STATE_USERNAME_GIVEN = 1;
    public static final int STATE_AUTHENTICATED = 2;

    private Pattern pUSER = Pattern.compile("^USER\\s+(?<name>.+)$", Pattern.CASE_INSENSITIVE);
    private Pattern pPASS = Pattern.compile("^PASS\\s+(?<password>.+)$", Pattern.CASE_INSENSITIVE);
    private Pattern pRETR = Pattern.compile("^RETR\\s+(?<index>\\d+)$", Pattern.CASE_INSENSITIVE);
    private Pattern pDELE = Pattern.compile("^DELE\\s+(?<index>\\d+)$", Pattern.CASE_INSENSITIVE);
    private String pQUIT = "^(?i:QUIT)$";

    private MailServer server;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private int state;
    private boolean running;
    private User user;
    private String username;

    public MailClient(MailServer server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.running = false;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            state = STATE_CONNECTED;
            server.getTALog().append("Client verbunden an " + SocketUtils.getSocketIP(socket) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleStateConnected(String input) {
        Matcher m = pUSER.matcher(input);

        // If USER command issued
        if (m.find()) {
            username = m.group("name");
            user = server.findUserByName(username);

            // Security: Accept no matter user really exists or not
            writer.printf("+OK user %s is a real hoopy frood\n\r", username);
            state = STATE_USERNAME_GIVEN;
            return;
        } else if (input.matches(pQUIT)) {
            stopThread();
        } else {
            writer.println("-ERROR Unknown command");
            writer.println("The following commands are available:\n\r" + "USER <username>: authenticates the " +
                    "user\n\r" + "QUIT: ends the session");
        }
    }

    private void handleStateUsernameGiven(String input) {
        Matcher m1 = pPASS.matcher(input);

        // Password given
        if (m1.find()) {
            String password = m1.group("password");
            if (user != null && user.checkPassword(password)) {
                // User authenticated successfully
                writer.println("+OK");
                state = STATE_AUTHENTICATED;
            } else {
                // User not authenticated, revert to initial state
                writer.printf("-ERROR sorry, %s doesn't get his mail here\n\r", username);
                state = STATE_CONNECTED;
            }
        } else if (input.matches(pQUIT)) {
            stopThread();
        } else {
            writer.println("-ERROR Unknown command");
        }
    }

    private void handleStateAuthenticated(String input) {
        Matcher mRETR = pRETR.matcher(input);
        Matcher mDELE = pDELE.matcher(input);

        if (input.matches("^(?i:NOOP)$")) {
            // Command NO-OPeration
            writer.println("+OK");
        } else if (input.matches("^(?i:STAT$)")) {
            // Command STATistics
            writer.printf("+OK %d %d\n\r", user.getMailCount(), user.getMailOctets());
        } else if (input.matches("^(?i:LIST)$")) {
            // Command LIST
            writer.printf("+OK mailbox has %d messages (%d octets)\n\r", user.getMailCount(), user.getMailOctets());
            for (int i = 0; i < user.getMails().size(); i++) {
                Mail m = user.getMails().get(i);
                if (!m.isDeleted()) {
                    writer.printf("%d %d\n\r", i + 1, m.getSize());
                }
            }
            writer.println(".");
        } else if (input.matches("^(?i:RSET$)")) {
            // Command ReSET
            // go through all mails and set their deletion state to false
            for (int i = 0; i < user.getMails().size(); i++) {
                Mail m = user.getMails().get(i);
                m.markDeleted(false);
                user.getMails().setElementAt(m, i);
            }

            writer.println("+OK all deletions reversed");
        } else if (mRETR.find()) {
            // Command RETRieve
            // check if index is valid
            int index = Integer.parseInt(mRETR.group("index")) - 1;
            if (index < 0 || index >= user.getMailCount()) {
                writer.printf("-ERROR invalid index %d\n\r", index + 1);
                return;
            }

            // get message with index
            Mail m = user.getMails().get(index);
            if (m.isDeleted()) {
                // if message is marked deleted, display error
                writer.println("+ERROR invalid message");
            } else {
                // send formatted message
                writer.println("+OK message follows\n" + m.format());
            }
        } else if (mDELE.find()) {
            // Command DELEte
            int index = Integer.parseInt(mDELE.group("index")) - 1;
            if (index < 0 || index >= user.getMailCount()) {
                writer.printf("-ERROR invalid index %d\n\r", index + 1);
                return;
            }

            Mail m = user.getMails().get(index);
            if (m.isDeleted()) {
                writer.println("+ERROR invalid message");
            } else {
                m.markDeleted(true);
                writer.println("+OK message marked for delete");
                user.getMails().setElementAt(m, index);
                // server.getWindow().refreshMessageList();
            }
        } else if (input.matches(pQUIT)) {
            // Command QUIT
            stopThread();
        } else {
            writer.println("-ERROR Unknown command");
        }
    }

    public void stopThread() {
        // really remove all messages marked as deleted from list
        if (user != null) {
            DefaultListModel<Mail> mails = user.getMails();
            for (int i = mails.size() - 1; i >= 0; i--) {
                Mail m = mails.get(i);

                if (m.isDeleted()) {
                    mails.removeElementAt(i);
                }
            }
        }

        writer.println("+OK POP3 server signing off");
        running = false;
    }

    public void run() {
        running = true;
        writer.format("+OK POP3 server ready <%s>\n\r", server.LOCAL_DOMAIN);
        String input;

        try {
            while (running && (input = reader.readLine()) != null) {
                switch (state) {
                    case STATE_CONNECTED:
                        handleStateConnected(input);
                        break;
                    case STATE_USERNAME_GIVEN:
                        handleStateUsernameGiven(input);
                        break;
                    case STATE_AUTHENTICATED:
                        handleStateAuthenticated(input);
                        break;
                }
            }

            reader.close();
            writer.close();
            socket.close();
            server.getTALog().append("Clientverbindung beendet an " + SocketUtils.getSocketIP(socket) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}