package net.jay.palm.ui.component;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class InnerConsole extends JEditorPane {
    public InnerConsole() {
        this.setEditable(false);
        this.setContentType("text/html");
        this.setFont(new Font("Consolas", Font.PLAIN, 14));
    }

    public void print(String message, Color color) {
        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setForeground(sas, color);

        try {
            getDocument().insertString(getDocument().getLength(), message, sas);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        FontMetrics fm = getFontMetrics(getFont());
        int colWidth = fm.charWidth('m');
        int rowHeight = fm.getHeight();
        d.width = Math.max(d.width, rowHeight * 24);
        d.height = Math.max(d.height, colWidth * 80);
        return d;
    }
}
