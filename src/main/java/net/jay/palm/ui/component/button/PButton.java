package net.jay.palm.ui.component.button;

import javax.swing.*;
import java.awt.*;

public class PButton extends JButton {
    public PButton() {
        super();
    }

    public PButton(Icon icon) {
        super(icon);
    }

    public PButton(String text) {
        super(text);
    }

    public PButton(Action a) {
        super(a);
    }

    public PButton(String text, Icon icon) {
        super(text, icon);
    }
}
