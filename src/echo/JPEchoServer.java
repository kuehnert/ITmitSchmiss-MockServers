package echo;

import mainwindow.Main;
import utils.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JPEchoServer extends JPanel {
    private static final String LABEL_START = "Starte Echo-Server";
    private static final String LABEL_END = "Stoppe Echo-Server";

    private final EchoServer echoServer;
    private JButton bStartStop;
    private JPanel pMain;
    private JLabel lbPort;
    private JTextArea taLog;
    private boolean running;

    public JPEchoServer() {
        super();
        bStartStop.setText(LABEL_START);
        taLog.setFont(SwingUtils.getFont());
        lbPort.setText("Port: " + Main.PORT_ECHO);
        running = false;
        add(pMain);
        echoServer = new EchoServer(taLog);
        bStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (running) {
                    echoServer.stop();
                    bStartStop.setText(LABEL_START);
                } else {
                    echoServer.start();
                    bStartStop.setText(LABEL_END);
                }

                running = !running;
            }
        });
    }
}
