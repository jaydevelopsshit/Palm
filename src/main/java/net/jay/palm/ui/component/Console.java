package net.jay.palm.ui.component;

import javax.swing.*;
import java.awt.*;

public class Console extends JScrollPane {
    private final InnerConsole inner;

    public Console() {
        super(new InnerConsole());
        this.inner = (InnerConsole)getViewport().getView();
        this.setBorder(null);
    }

    public void log(String message) {
        log(message, Color.white);
    }

    public void log(String message, Color color) {
        inner.print("[Log] " + message + System.lineSeparator(), color);
    }

    public void success(String message) {
        log(message, Color.green);
    }

    public void warn(String message) {
        inner.print("[Warn] " + message + System.lineSeparator(), Color.yellow);
    }

    public void error(String message) {
        inner.print("[Error] " + message + System.lineSeparator(), Color.red);
    }
}
