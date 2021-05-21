package utils;

import javax.swing.*;
import javax.swing.text.BadLocationException;

public class JLogArea extends JTextArea {
    private static final int SCROLL_BUFFER_SIZE = 200;

    public JLogArea() {
        super();
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    public void append(String newText) {
        super.append(newText);
        setCaretPosition(getDocument().getLength());

        int numLines = getLineCount() - SCROLL_BUFFER_SIZE;
        if (numLines > 0) {
            try {
                int posOfLastLineToTrunk = getLineEndOffset(numLines - SCROLL_BUFFER_SIZE / 2);
                replaceRange("", 0, posOfLastLineToTrunk);
            } catch (BadLocationException e) {
                // nich schlimm
            }
        }
    }

    public void clear() {
        super.setText(null);
    }
}
