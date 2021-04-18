package pop3;

import java.util.Date;

public class Mail {
    private final String sender;
    private final String recipient;
    private final String title;
    private final String content;
    private final Date sent;
    private boolean deleted;

    public Mail(String sender, String recipient, String title, String content, Date sent) {
        this.sender = sender;
        this.recipient = recipient;
        this.title = title;
        this.content = content;
        this.sent = sent;
        deleted = false;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String toString() {
        return title + (deleted ? " (geloescht)" : "");
    }

    public String toHTML() {
        return String.format("<HTML><b>Date:</b> %s<br><b>From:</b> %s<br><b>To:</b> %s<br><b>Subject:</b> %s<br><br>%s</HTML>", sent, sender,
                recipient, title, content);
    }

    public int getSize() {
        return content.length();
    }

    public String format() {
        String out = String.format("Date:     %s\n\r", sent.toString());
        out += String.format("From:     %s\n\r", sender);
        out += String.format("To:       %s\n\r", recipient);
        out += String.format("Subject:  %s\n\r", title);
        out += content + "\n\r.";
        return out;
    }

    public void markDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
