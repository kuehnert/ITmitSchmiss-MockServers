package echo;

import mainwindow.Main;
import utils.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JPEchoServer extends JPanel {
    private JButton bStartStop;
    private final EchoServer echoServer;
    private JPanel pMain;
    private JLabel lbPort;
    private JTextArea taLog;

    public JPEchoServer() {
        super(); taLog.setFont(SwingUtils.getFont()); lbPort.setText("Port: " + Main.PORT_ECHO); add(pMain);
        echoServer = new EchoServer(taLog); bStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (bStartStop.getText().equals("Stop")) {
                    echoServer.stop(); bStartStop.setText("Start");
                } else {
                    echoServer.start(); bStartStop.setText("Stop");
                }
            }
        });
    }
}
