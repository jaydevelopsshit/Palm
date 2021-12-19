package net.jay.palm.ui.component;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class JHexArea extends JTextArea {
    private static final String hexDigits = "0123456789abcdef";

    @Override
    protected Document createDefaultModel() {
        return new HexDocument();
    }

    private static class HexDocument extends PlainDocument {
        private int spaceCharThreshold = 0;

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if(str == null) return;

            if(!str.toLowerCase().matches("[" + hexDigits + "]+")) {
                List<Character> chars = str.chars().mapToObj(c -> (char)c).collect(Collectors.toList());
                List<Character> goodChars = new ArrayList<>();

                for(Character character : chars) {
                    if(character.toString().toLowerCase().matches("[" + hexDigits + "]+")) goodChars.add(character);
                }

                char[] correctedChars = new char[goodChars.size()];

                for(int i = 0; i < correctedChars.length; i++) {
                    correctedChars[i] = goodChars.get(i);
                }

                super.insertString(offs, String.valueOf(correctedChars).toUpperCase(), a);
            } else {
                String string = str.toUpperCase();

                if(spaceCharThreshold >= 2) {
                    string = " " + string;
                    spaceCharThreshold = 0;
                }

                spaceCharThreshold++;

                super.insertString(offs, string, a);
            }
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {

            if(getLength() == 1) {
                super.remove(offs, len);
                return;
            }
            if(getText(offs - 1, 1).equals(" ")) {
                super.remove(offs - 1, len + 1);
                spaceCharThreshold = 2;
            } else {
                super.remove(offs, len);
            }
        }
    }
}
