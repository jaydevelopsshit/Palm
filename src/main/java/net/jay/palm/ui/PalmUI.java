package net.jay.palm.ui;

import com.bulenkov.darcula.DarculaLaf;
import net.jay.palm.ui.screen.MainScreen;
import net.jay.palm.ui.screen.Screen;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLookAndFeel;
import java.awt.*;

public class PalmUI {
    private static final BasicLookAndFeel DARCULA = new DarculaLaf();

    public Thread uiThread;

    public void init() {
        try {
            UIManager.setLookAndFeel(DARCULA);
        } catch(UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        MainScreen mainScreen = new MainScreen();

        mainScreen.setSize(800, 600);
        mainScreen.setVisible(true);
    }
}
