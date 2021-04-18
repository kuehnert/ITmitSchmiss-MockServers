package pop3;

import mainwindow.Main;
import utils.SwingUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JPPOP3Server extends JPanel {
    private static final String BUTTON_START_MSG = "Starte POP3-Mail-Server";
    private static final String BUTTON_STOP_MSG = "Stoppe POP3-Mail-Server";

    private MailServer mailServer;
    private JButton bStartStop;
    private JPanel pMain;
    private JLabel lbPort;
    private JList lMessages;
    private JTextPane taMessage;
    private JList lUsers;
    private JTextArea taLog;
    private boolean isRunning;

    public JPPOP3Server() {
        super();
        isRunning = false;
        bStartStop.setText(BUTTON_START_MSG);
        lbPort.setText("Port: " + Main.PORT_POP3);
        taLog.setFont(SwingUtils.getFont());
        taMessage.setFont(SwingUtils.getFont());
        add(pMain);

        bStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                startStop();
            }
        });
        lUsers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    changeSelectedUser();
                }
            }
        });
        lMessages.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    changeSelectedMail();
                }
            }
        });
    }

    public void startStop() {
        if (isRunning) {
            lUsers.setEnabled(false);
            lMessages.setEnabled(false);
            taMessage.setEnabled(false);
            mailServer.stopThread();
            mailServer = null;
            bStartStop.setText(BUTTON_START_MSG);
            isRunning = false;
        } else {
            mailServer = new MailServer(this);
            lUsers.setModel(mailServer.getUsers());
            lUsers.setEnabled(true);
            lMessages.setEnabled(true);
            taMessage.setEnabled(true);
            mailServer.start();
            isRunning = true;
            bStartStop.setText(BUTTON_STOP_MSG);
        }
    }

    private void changeSelectedUser() {
        User newUser = (User) lUsers.getSelectedValue();
        if (newUser != null) {
            lMessages.setModel(newUser.getMails());
            lMessages.setSelectedIndex(0);
        }
    }

    private void changeSelectedMail() {
        Mail newMail = (Mail) lMessages.getSelectedValue();
        if (newMail == null) {
            taMessage.setText("");
        } else {
            taMessage.setText(newMail.toHTML());
        }
    }

    public JTextArea getLog() {
        return taLog;
    }
}
