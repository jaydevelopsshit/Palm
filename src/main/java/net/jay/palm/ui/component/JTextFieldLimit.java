package net.jay.palm.ui.component;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class JTextFieldLimit extends JTextField {
    private final int limit;

    public JTextFieldLimit(int limit) {
        super();
        this.limit = limit;
    }

    @Override
    protected Document createDefaultModel() {
        return new LimitDocument();
    }

    private class LimitDocument extends PlainDocument {
        @Override
        public void insertString(int offset, String  str, AttributeSet attr) throws BadLocationException {
            if(str == null) return;

            if((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }

    }

}

