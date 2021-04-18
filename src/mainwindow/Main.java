package mainwindow;

import pop3.JPPOP3Server;

import javax.swing.*;
import java.nio.charset.Charset;

public class Main extends JFrame {
    public static final int PORT_TIME = 10013;
    public static final int PORT_ECHO = 10007;
    public static final int PORT_POP3 = 10110;
    public static final int PORT_SMTP = 10025;

    public static final String VERSION = "1.1.0";
    private JPanel pMain;
    private JPPOP3Server JPPOP3Server;
    private JTabbedPane tpTabs;

    public Main() {
        super("Network Mock-Server (" + VERSION + ")");
        add(pMain);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }

        setSize(800, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLocationRelativeTo(null); setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println("Charset: " + Charset.defaultCharset());
        new Main();
    }
}
