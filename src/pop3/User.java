package pop3;

import javax.swing.*;

public class User {
    private final String name;
    private final String password;
    private final DefaultListModel<Mail> mails;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        mails = new DefaultListModel<>();
    }

    public String getName() {
        return name;
    }

    public DefaultListModel<Mail> getMails() {
        return mails;
    }

    @Override
    public String toString() {
        return String.format("%s / \"%s\"", name, password);
    }

    public boolean checkPassword(String otherPassword) {
        return this.password != null && this.password.equals(otherPassword);
    }

    public int getMailCount() {
        return mails.getSize();
    }

    public int getMailOctets() {
        int octets = 0;

        for (int i = 0; i < mails.getSize(); i++) {
            octets += mails.get(i).getSize();
        }

        return octets;
    }
}
