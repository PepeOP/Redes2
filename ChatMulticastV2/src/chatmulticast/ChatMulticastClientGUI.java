package chatmulticast;


import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

public class ChatMulticastClientGUI {
    private static final String MULTICAST_GROUP = "230.1.1.1";
    private static final int MULTICAST_PORT = 4000;
    private static final int BUFFER_SIZE = 1024;

    private static final Map<String, String> emoticons = new HashMap<>();

    static {
        emoticons.put(":)", "images/imagen_beso.jpg");
        emoticons.put(":o", "images/amor.png");
        emoticons.put(":S", "images/enojo.png");
        emoticons.put("_loco_", "images/loco.gif");
        emoticons.put("_Homero_", "images/homero.gif");
    }

    private MulticastSocket socket;
    private InetAddress group;
    private String username;
    private JTextPane chatPane;
    private JTextField messageField;

    public ChatMulticastClientGUI(String username) throws IOException {
        this.username = username;
        this.group = InetAddress.getByName(MULTICAST_GROUP);
        this.socket = new MulticastSocket(MULTICAST_PORT);
        socket.joinGroup(group);

        JFrame frame = new JFrame("Chat Multicast");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        chatPane = new JTextPane();
        chatPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatPane);

        messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String message = messageField.getText();
                    if (message.startsWith("/privado")) {
                        String[] parts = message.split(" ", 3);
                        if (parts.length == 3) {
                            String userTo = parts[1];
                            String msg = parts[2];
                            for (Map.Entry<String, String> entry : emoticons.entrySet()) {
                                msg = msg.replace(entry.getKey(), "<" + entry.getValue() + ">");
                            }
                            sendMessage("<privado><" + username + "><" + userTo + ">" + msg);
                            // Solo agregar el mensaje a la interfaz del emisor y no enviarlo a través del socket
                            appendText("[Privado a " + userTo + "]: ", Color.MAGENTA);
                            insertEmoticons(msg);
                            appendText("\n", Color.MAGENTA);
                        } else {
                            appendText("Formato incorrecto. Uso: /privado <usuario> <mensaje>\n", Color.RED);
                        }
                    } else if (message.equals("/salir")) {
                        sendMessage("<fin>" + username);
                        socket.leaveGroup(group);
                        socket.close();
                        System.exit(0);
                    } else {
                        for (Map.Entry<String, String> entry : emoticons.entrySet()) {
                            message = message.replace(entry.getKey(), "<" + entry.getValue() + ">");
                        }
                        sendMessage("<msj><" + username + ">" + message);
                    }
                    messageField.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(messageField, BorderLayout.SOUTH);

        frame.setVisible(true);

        sendMessage("<inicio>" + username);

        new Thread(this::receiveMessages).start();
    }

    private void sendMessage(String message) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, MULTICAST_PORT);
        socket.send(packet);
    }

    private void receiveMessages() {
        byte[] buffer = new byte[BUFFER_SIZE];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                final String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                SwingUtilities.invokeLater(() -> {
                    if (receivedMessage.startsWith("<inicio>")) {
                        appendText(receivedMessage.substring(8) + " ha entrado al chat\n", Color.BLUE);
                    } else if (receivedMessage.startsWith("<msj>")) {
                        int userEnd = receivedMessage.indexOf('>', 5);
                        String user = receivedMessage.substring(5, userEnd);
                        appendText(user + ": ", Color.BLACK);
                        insertEmoticons(receivedMessage.substring(userEnd + 1));
                        appendText("\n", Color.BLACK);
                    } else if (receivedMessage.startsWith("<privado>")) {
                        // Procesar mensajes privados
                        int startUserFrom = receivedMessage.indexOf('<', 9) + 1;
                        int endUserFrom = receivedMessage.indexOf('>', startUserFrom);
                        String userFrom = receivedMessage.substring(startUserFrom, endUserFrom);

                        int startUserTo = endUserFrom + 2;
                        int endUserTo = receivedMessage.indexOf('>', startUserTo);
                        String userTo = receivedMessage.substring(startUserTo, endUserTo);

                        String msg = receivedMessage.substring(endUserTo + 1);

                        // Solo mostrar el mensaje si es para el usuario actual
                        if (userTo.equals(username)) {
                            appendText("[Privado de " + userFrom + "]: ", Color.MAGENTA);
                            insertEmoticons(msg);
                            appendText("\n", Color.MAGENTA);
                        }
                    } else if (receivedMessage.startsWith("<fin>")) {
                        appendText(receivedMessage.substring(5) + " ha salido del chat\n", Color.BLUE);
                    } else if (receivedMessage.contains("<msj>")) {
                        String modifiedMessage = receivedMessage;
                        for (Map.Entry<String, String> entry : emoticons.entrySet()) {
                            modifiedMessage = modifiedMessage.replace(entry.getKey(), "<" + entry.getValue() + ">");
                        }
                        int userEnd = modifiedMessage.indexOf('>', 5);
                        String user = modifiedMessage.substring(5, userEnd);
                        appendText(user + ": ", Color.BLACK);
                        insertEmoticons(modifiedMessage.substring(userEnd + 1));
                        appendText("\n", Color.BLACK);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void appendText(String text, Color color) {
        StyledDocument doc = chatPane.getStyledDocument();
        Style style = chatPane.addStyle("I'm a Style", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void insertEmoticons(String message) {
        StyledDocument doc = chatPane.getStyledDocument();
        int start = 0;
        int end;
        while ((end = message.indexOf('<', start)) >= 0) {
            int close = message.indexOf('>', end);
            if (close < 0) break;
            appendText(message.substring(start, end), Color.BLACK);
            String imagePath = message.substring(end + 1, close);
            try {
                Style style = chatPane.addStyle("Image Style", null);
                ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(imagePath));
                if (icon.getIconWidth() > 0) {
                    StyleConstants.setIcon(style, icon);
                    doc.insertString(doc.getLength(), "ignored text", style);
                } else {
                    appendText("[Imagen no encontrada: " + imagePath + "]", Color.RED);
                }
            } catch (Exception e) {
                appendText("[Error al cargar la imagen: " + imagePath + "]", Color.RED);
                e.printStackTrace();
            }
            start = close + 1;
        }
        appendText(message.substring(start), Color.BLACK);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String username = JOptionPane.showInputDialog("Introduce tu nombre de usuario:");
            if (username != null && !username.trim().isEmpty()) {
                try {
                    new ChatMulticastClientGUI(username);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}








