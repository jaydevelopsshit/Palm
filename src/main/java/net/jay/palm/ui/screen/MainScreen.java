package net.jay.palm.ui.screen;

import net.jay.palm.Palm;
import net.jay.palm.Tasks;
import net.jay.palm.io.BinaryReader;
import net.jay.palm.io.BinaryWriter;
import net.jay.palm.ui.component.Console;
import net.jay.palm.ui.component.JHexArea;
import net.jay.palm.ui.component.JTextFieldLimit;
import net.jay.palm.ui.component.button.DefaultButtonSize;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class MainScreen extends Screen {
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    private JPanel leftPane;
    private JPanel rightPane;

    private Console console;
    private JButton connectButton;

    private int lastHeight;

    @Override
    public void setupUIElements() {
        lastHeight = 600;

        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        // Create panes
        leftPane = new JPanel();
        rightPane = new JPanel();

        // Configure panes
        SpringLayout leftPaneLayout = new SpringLayout();
        SpringLayout rightPaneLayout = new SpringLayout();
        leftPane.setLayout(leftPaneLayout);
        leftPane.setPreferredSize(new Dimension(210, 0));
        leftPane.setBorder(new LineBorder(Color.gray));
        rightPane.setLayout(rightPaneLayout);

        // Create and configure left pane components
        JLabel ipLabel = new JLabel("Ip/Port");
        connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(205, 35));
        JTextField ipField = new JTextField();
        ipField.setToolTipText("Domain/IP");
        ipField.setPreferredSize(new Dimension(145, 25));
        JTextFieldLimit portField = new JTextFieldLimit(5);
        portField.setToolTipText("Port");
        portField.setPreferredSize(new Dimension(58, 25));
        JButton sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        sendButton.setPreferredSize(new Dimension(203, 35));

        // Bytes panel
        JPanel bytesPanel = new JPanel();
        bytesPanel.setEnabled(false);
        bytesPanel.setLayout(new BoxLayout(bytesPanel, BoxLayout.Y_AXIS));
        JHexArea hexArea = new JHexArea();
        hexArea.setEnabled(false);
        hexArea.setLineWrap(true);
        JTextArea stringArea = new JTextArea();
        stringArea.setEnabled(false);
        stringArea.setLineWrap(true);
        // TODO Align hex and text to the left somehow
        JLabel hex = new JLabel("Hex");
        bytesPanel.add(hex);
        bytesPanel.add(hexArea);
        JLabel text = new JLabel("Text");
        bytesPanel.add(text);
        bytesPanel.add(stringArea);
        bytesPanel.setPreferredSize(new Dimension(205, 400));

        // Left Pane Layout constraints
        leftPaneLayout.putConstraint(SpringLayout.NORTH, ipField, 0, SpringLayout.SOUTH, ipLabel);
        leftPaneLayout.putConstraint(SpringLayout.NORTH, portField, 0, SpringLayout.SOUTH, ipLabel);
        leftPaneLayout.putConstraint(SpringLayout.WEST, portField, 0, SpringLayout.EAST, ipField);
        leftPaneLayout.putConstraint(SpringLayout.NORTH, connectButton, 0, SpringLayout.SOUTH, ipField);
        leftPaneLayout.putConstraint(SpringLayout.NORTH, bytesPanel, 5, SpringLayout.SOUTH, connectButton);
        leftPaneLayout.putConstraint(SpringLayout.NORTH, sendButton, 0, SpringLayout.SOUTH, bytesPanel);

        // Add left pane components
        leftPane.add(ipLabel);
        leftPane.add(ipField);
        leftPane.add(portField);
        leftPane.add(connectButton);
        leftPane.add(bytesPanel);
        leftPane.add(sendButton);

        // Create and configure right pane components
        console = new Console();
        Palm.getInst().uiConsole = console;
        SpringLayout rbLayout = new SpringLayout();
        JPanel rightButtonsPanel = new JPanel(rbLayout);
        rightButtonsPanel.setBorder(new LineBorder(Color.gray));
        rightButtonsPanel.setPreferredSize(new Dimension(120, 600));
        JButton disconnectButton = new JButton("Disconnect");
        disconnectButton.setPreferredSize(new Dimension(118, 35));
        disconnectButton.setEnabled(false);
        rightButtonsPanel.add(disconnectButton);
        rbLayout.putConstraint(SpringLayout.SOUTH, disconnectButton, 2, SpringLayout.SOUTH, rightButtonsPanel);

        // Right pane layout constraints
        rightPaneLayout.putConstraint(SpringLayout.EAST, rightButtonsPanel, 0, SpringLayout.EAST, rightPane);

        // Add right pane components
        rightPane.add(console);
        rightPane.add(rightButtonsPanel);

        // Add panes
        add(leftPane, BorderLayout.LINE_START);
        add(rightPane, BorderLayout.LINE_END);

        // Other
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                rightPane.setPreferredSize(new Dimension(e.getComponent().getWidth() - 210, 0));
                console.setPreferredSize(new Dimension(e.getComponent().getWidth() - 330, e.getComponent().getHeight()));
                rightButtonsPanel.setPreferredSize(new Dimension(120, e.getComponent().getHeight() - 35));
                bytesPanel.setPreferredSize(new Dimension(bytesPanel.getPreferredSize().width, e.getComponent().getHeight() - 154));
            }
        });

        addChangeListener(hexArea, (e) -> {
            String content = hexArea.getText().replaceAll(" ", "");

            if(content.length() % 2 != 0) return;

            byte[] bytes = hexStringToByteArray(content);

            stringArea.setText(new String(bytes));
        });

        addChangeListener(stringArea, (e) -> {
            byte[] bytes = stringArea.getText().getBytes();
            hexArea.setText(bytesToHex(bytes));
        });

        connectButton.addActionListener(e -> {
            String ip = ipField.getText().trim();
            int port;

            if(ip.isEmpty()) {
                console.error("Invalid domain/ip");
                return;
            }

            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch(NumberFormatException exception) {
                console.error("Invalid port");
                return;
            }

            if(port > 65535) {
                console.error("Invalid port");
                return;
            }

            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            ipField.setEnabled(false);
            portField.setEnabled(false);

            Palm.getInst().addTask(() -> {
                Socket connection = null;
                try {
                    boolean running = true;
                    connection = new Socket();
                    Palm.getInst().connection = connection;
                    connection.connect(new InetSocketAddress(ip, port), 12000);
                    if(connection.isClosed()) return;
                    hexArea.setEnabled(true);
                    sendButton.setEnabled(true);
                    console.success("Connected to server");
                    BinaryReader reader = new BinaryReader(connection.getInputStream());
                    BinaryWriter writer = new BinaryWriter(connection.getOutputStream());

                    while(running) {
                        Thread.sleep(20);
                        if(Palm.getInst().connection == null) {
                            running = false;
                            return;
                        }
                        if(reader.available() <= 0) continue;
                        byte[] buffer = new byte[reader.available()];
                        reader.read(buffer);
                        console.log("Data received: " + Arrays.toString(buffer));
                    }
                } catch(Exception exception) {
                    exception.printStackTrace();
                    if(exception.getMessage().contains("Socket closed")) {}
                    else if(exception.getMessage().contains("timed out")) console.error("Couldn't connect to server: Timed out");
                    else if(exception instanceof UnknownHostException) console.error("Couldn't connect to server: Unknown host");
                    else console.error("Couldn't connect to server: Exception thrown");
                    connectButton.setEnabled(true);
                    ipField.setEnabled(true);
                    portField.setEnabled(true);
                    disconnectButton.setEnabled(false);
                    hexArea.setEnabled(false);
                    sendButton.setEnabled(false);

                    try {
                        connection.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });

        sendButton.addActionListener(e -> {
            if(hexArea.getText().length() % 2 != 0) {
                console.warn("Incomplete hex");
                return;
            }

            try {
                BinaryWriter writer = new BinaryWriter(Palm.getInst().connection.getOutputStream());

                String hexString = hexArea.getText().replaceAll(" ", "");

                byte[] data = hexStringToByteArray(hexString);

                writer.writeBytes(data);
                writer.flush();

                console.log("Sending data: " + Arrays.toString(data));
            } catch (IOException ex) {
                if(ex.getMessage().contains("Connection reset")) {
                    connectButton.setEnabled(true);
                    ipField.setEnabled(true);
                    portField.setEnabled(true);
                    disconnectButton.setEnabled(false);
                    hexArea.setEnabled(false);
                    sendButton.setEnabled(false);
                    console.error("Server ended connection");
                    return;
                }
                ex.printStackTrace();
                console.error("Error sending data: Exception thrown");
            }
        });

        disconnectButton.addActionListener(e -> {
            try {
                Palm.getInst().connection.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            Palm.getInst().connection = null;
            connectButton.setEnabled(true);
            ipField.setEnabled(true);
            portField.setEnabled(true);
            disconnectButton.setEnabled(false);
            hexArea.setEnabled(false);
            sendButton.setEnabled(false);
            console.log("Disconnected from server", Color.red);
        });
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }


    /**
     * Installs a listener to receive notification when the text of any
     * {@code JTextComponent} is changed. Internally, it installs a
     * {@link DocumentListener} on the text component's {@link Document},
     * and a {@link PropertyChangeListener} on the text component to detect
     * if the {@code Document} itself is replaced.
     *
     * @param text any text component, such as a {@link JTextField}
     *        or {@link JTextArea}
     * @param changeListener a listener to receieve {@link ChangeEvent}s
     *        when the text is changed; the source object for the events
     *        will be the text component
     * @throws NullPointerException if either parameter is null
     */
    public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(changeListener);
        DocumentListener dl = new DocumentListener() {
            private int lastChange = 0, lastNotifiedChange = 0;

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lastChange++;
                SwingUtilities.invokeLater(() -> {
                    if (lastNotifiedChange != lastChange) {
                        lastNotifiedChange = lastChange;
                        changeListener.stateChanged(new ChangeEvent(text));
                    }
                });
            }
        };
        text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
            Document d1 = (Document)e.getOldValue();
            Document d2 = (Document)e.getNewValue();
            if (d1 != null) d1.removeDocumentListener(dl);
            if (d2 != null) d2.addDocumentListener(dl);
            dl.changedUpdate(null);
        });
        Document d = text.getDocument();
        if (d != null) d.addDocumentListener(dl);
    }
}
