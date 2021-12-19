package net.jay.palm.ui.screen;

import javax.swing.*;
import java.awt.*;

public abstract class Screen extends JFrame {
    public Screen() throws HeadlessException {
        super("Palm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUIElements();
    }

    public Screen(GraphicsConfiguration gc) {
        super(gc);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUIElements();
    }

    public Screen(String title) throws HeadlessException {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUIElements();
    }

    public Screen(String title, GraphicsConfiguration gc) {
        super(title, gc);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUIElements();
    }

    public abstract void setupUIElements();
}
