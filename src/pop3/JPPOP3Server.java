package pop3;

import mainwindow.Main;
import utils.SwingUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JPPOP3Server extends JPanel {
    private JButton bStartStop;
    private final MailServer mailServer;
    private JPanel pMain;
    private JLabel lbPort;
    private JList lMessages;
    private JTextPane taMessage;
    private JList lUsers;
    private JTextArea taLog;

    public JPPOP3Server() {
        super(); lbPort.setText("Port: " + Main.PORT_POP3); taLog.setFont(SwingUtils.getFont());
        taMessage.setFont(SwingUtils.getFont()); add(pMain); mailServer = new MailServer(taLog);
        lUsers.setModel(mailServer.getUsers());

        bStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (mailServer.isRunning()) {
                    mailServer.stopThread(); bStartStop.setText("Start");
                } else {
                    mailServer.start(); bStartStop.setText("Stop");
                }
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

    private void changeSelectedUser() {
        User newUser = (User) lUsers.getSelectedValue();
        lMessages.setModel(newUser.getMails());
        lMessages.setSelectedIndex(0);
    }

    private void changeSelectedMail() {
        Mail newMail = (Mail) lMessages.getSelectedValue();
        if (newMail == null) {
            taMessage.setText("");
        } else {
            taMessage.setText(newMail.toHTML());
        }
    }
}
