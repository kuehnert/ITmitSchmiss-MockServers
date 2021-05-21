package daytime;

import mainwindow.Main;
import utils.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JPDaytimeServer extends JPanel {
    private static final String LABEL_START = "Starte Daytime-Server";
    private static final String LABEL_STOP = "Stoppe Daytime-Server";

    private final TimeServer timeServer;
    private JButton bStartStop;
    private utils.JLogArea taLog;
    private JPanel pMain;
    private JLabel lbPort;
    private boolean running;

    public JPDaytimeServer() {
        super();
        running = false;
        bStartStop.setText(LABEL_START);
        lbPort.setText("Port: " + Main.PORT_TIME);
        taLog.setFont(SwingUtils.getFont());
        add(pMain);
        timeServer = new TimeServer(taLog);
        bStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (running) {
                    timeServer.stop();
                    bStartStop.setText(LABEL_START);
                } else {
                    timeServer.start();
                    bStartStop.setText(LABEL_STOP);
                }

                running = !running;
            }
        });
    }
}
