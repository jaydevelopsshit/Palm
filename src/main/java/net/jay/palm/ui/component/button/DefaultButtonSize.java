package net.jay.palm.ui.component.button;

import java.awt.*;

public enum DefaultButtonSize {
    Small(80, 20),
    Medium(60, 30),
    Large(160, 40),
    Override(-1, -1);

    public final int width;
    public final int height;
    public final Dimension dimension;

    DefaultButtonSize(int width, int height) {
        this.width = width;
        this.height = height;
        dimension = new Dimension(width, height);
    }
}
