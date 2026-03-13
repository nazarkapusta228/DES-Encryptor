package pl.krypto.gui;

import pl.krypto.des.DES;
import pl.krypto.utils.ByteUtils;
import pl.krypto.utils.FileHandler;
import pl.krypto.utils.Padding;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class MainWindow extends JFrame {
    private TextPanel inputPanel;
    private TextPanel outputPanel;
    private JTextField keyField;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton loadFileButton;
    private JButton saveFileButton;
    private JButton clearButton;
    private JButton pasteFromOutputButton;
    private FileChooserHandler fileChooser;

    private byte[] currentFileData = null;

    public MainWindow() {
        setTitle("DES Encryptor/Decryptor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        fileChooser = new FileChooserHandler();
        initComponents();
        setupLayout();
        setupListeners();

        setSize(1000, 800);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        inputPanel = new TextPanel("Input (text or hex):");
        outputPanel = new TextPanel("Output:");

        keyField = new JTextField(20);
        keyField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        keyField.setText("0123456789ABCDEF");

        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");
        loadFileButton = new JButton("Load File");
        saveFileButton = new JButton("Save Output");
        clearButton = new JButton("Clear");
        pasteFromOutputButton = new JButton("← Paste from Output");
    }

    private void setupLayout() {
        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keyPanel.setBorder(BorderFactory.createTitledBorder("Key (8 bytes / 16 hex chars)"));
        keyPanel.add(new JLabel("Key:"));
        keyPanel.add(keyField);

        JPanel textPanels = new JPanel(new GridLayout(1, 2, 10, 0));
        textPanels.add(inputPanel);
        textPanels.add(outputPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(loadFileButton);
        buttonPanel.add(saveFileButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(pasteFromOutputButton);

        add(keyPanel, BorderLayout.NORTH);
        add(textPanels, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        encryptButton.addActionListener(e -> encrypt());
        decryptButton.addActionListener(e -> decrypt());
        loadFileButton.addActionListener(e -> loadFile());
        saveFileButton.addActionListener(e -> saveFile());
        clearButton.addActionListener(e -> clearAll());
        pasteFromOutputButton.addActionListener(e -> pasteFromOutput());
    }

    private void pasteFromOutput() {
        String outputText = outputPanel.getText().trim();
        if (!outputText.isEmpty()) {
            inputPanel.setText(outputText);
            currentFileData = null;
        }
    }

    private void encrypt() {
        try {
            byte[] key = getKeyBytes();
            byte[] input = getInputBytes();

            System.out.println("=== Encryption ===");
            System.out.println("Input length: " + input.length + " bytes");

            DES des = new DES(key);
            byte[] padded = Padding.addPadding(input);

            System.out.println("After padding: " + padded.length + " bytes");
            System.out.println("Number of blocks: " + (padded.length / 8));

            byte[] encrypted = new byte[padded.length];

            for (int i = 0; i < padded.length; i += 8) {
                byte[] block = new byte[8];
                System.arraycopy(padded, i, block, 0, 8);
                byte[] encryptedBlock = des.encryptBlock(block);
                System.arraycopy(encryptedBlock, 0, encrypted, i, 8);
                System.out.println("Block " + (i/8 + 1) + " encrypted");
            }

            String hex = ByteUtils.bytesToHex(encrypted);
            System.out.println("Encrypted bytes: " + encrypted.length);
            System.out.println("Encrypted hex: " + hex);

            outputPanel.setText(hex);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void decrypt() {
        try {
            byte[] key = getKeyBytes();
            byte[] input = getInputBytes();

            System.out.println("=== Decryption ===");
            System.out.println("Input length: " + input.length + " bytes");

            if (input.length % 8 != 0) {
                String msg = "Encrypted data must be multiple of 8 bytes.\n" +
                        "Current length: " + input.length + " bytes\n" +
                        "Expected: 8, 16, 24, 32... bytes\n\n" +
                        "If you pasted HEX, make sure you copied ALL bytes.\n" +
                        "Output should show multiple lines of 8 bytes each.";
                JOptionPane.showMessageDialog(this, msg);
                return;
            }

            DES des = new DES(key);
            byte[] decrypted = new byte[input.length];

            for (int i = 0; i < input.length; i += 8) {
                byte[] block = new byte[8];
                System.arraycopy(input, i, block, 0, 8);
                byte[] decryptedBlock = des.decryptBlock(block);
                System.arraycopy(decryptedBlock, 0, decrypted, i, 8);
                System.out.println("Block " + (i/8 + 1) + " decrypted");
            }

            byte[] unpadded = Padding.removePadding(decrypted);
            System.out.println("After removing padding: " + unpadded.length + " bytes");

            if (isTextFile(unpadded)) {
                String text = new String(unpadded, StandardCharsets.UTF_8);
                outputPanel.setText(text);
                System.out.println("Output as text: " + text);
            } else {
                String hex = ByteUtils.bytesToHex(unpadded);
                outputPanel.setText(hex);
                System.out.println("Output as hex: " + hex);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadFile() {
        File file = fileChooser.openFileChooser();
        if (file != null) {
            try {
                currentFileData = FileHandler.readFile(file);
                System.out.println("Loaded file: " + file.getName() + " (" + currentFileData.length + " bytes)");

                if (isTextFile(currentFileData)) {
                    inputPanel.setText(new String(currentFileData, StandardCharsets.UTF_8));
                } else {
                    inputPanel.setText(ByteUtils.bytesToHex(currentFileData));
                }

                setTitle("DES Encryptor/Decryptor - " + file.getName() + " (" + currentFileData.length + " bytes)");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
            }
        }
    }

    private boolean isTextFile(byte[] data) {
        if (data.length == 0) return true;

        int checkLength = Math.min(data.length, 1024);

        for (int i = 0; i < checkLength; i++) {
            int b = data[i] & 0xFF;
            // Дозволені: TAB, LF, CR, друковані символи (32-126)
            if (b != 9 && b != 10 && b != 13 && (b < 32 || b > 126)) {
                return false;
            }
        }
        return true;
    }

    private void saveFile() {
        File file = fileChooser.saveFileChooser();
        if (file != null && !outputPanel.getText().isEmpty()) {
            try {
                byte[] data;
                String outputText = outputPanel.getText().trim();

                // Перевіряємо чи це HEX (тільки hex символи, пробіли, перенесення)
                String cleanHex = outputText.replaceAll("\\s+", "");
                if (cleanHex.matches("[0-9A-Fa-f]+")) {
                    data = ByteUtils.hexToBytes(outputText);
                    System.out.println("Saving as hex: " + data.length + " bytes");
                } else {
                    data = outputText.getBytes(StandardCharsets.UTF_8);
                    System.out.println("Saving as text: " + data.length + " bytes");
                }

                FileHandler.writeFile(file, data);
                JOptionPane.showMessageDialog(this, "File saved successfully!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }

    private void clearAll() {
        inputPanel.clear();
        outputPanel.clear();
        keyField.setText("0123456789ABCDEF");
        currentFileData = null;
        setTitle("DES Encryptor/Decryptor");
    }

    private byte[] getKeyBytes() {
        String keyText = keyField.getText().trim();
        if (keyText.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        // Спершу пробуємо як HEX
        String cleanKey = keyText.replaceAll("\\s+", "");
        if (cleanKey.matches("[0-9A-Fa-f]+")) {
            try {
                byte[] key = ByteUtils.hexToBytes(cleanKey);
                if (key.length == 8) {
                    System.out.println("Using hex key: " + ByteUtils.bytesToHex(key));
                    return key;
                }
            } catch (Exception ignored) {}
        }

        // Пробуємо як текст
        byte[] key = keyText.getBytes(StandardCharsets.UTF_8);
        if (key.length != 8) {
            throw new IllegalArgumentException("Key must be 8 bytes (or 16 hex chars)\n" +
                    "Current key length: " + key.length + " bytes");
        }

        System.out.println("Using text key: " + keyText);
        return key;
    }

    private byte[] getInputBytes() {
        String text = inputPanel.getText();
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }

        // Якщо є завантажений файл, використовуємо його
        if (currentFileData != null) {
            System.out.println("Using file data: " + currentFileData.length + " bytes");
            return currentFileData;
        }

        // Розбиваємо на рядки і токени
        String[] lines = text.split("\n");
        StringBuilder hexBuilder = new StringBuilder();

        for (String line : lines) {
            // Видаляємо пробіли з початку і кінця рядка
            line = line.trim();
            if (!line.isEmpty()) {
                hexBuilder.append(line).append(" ");
            }
        }

        String cleanText = hexBuilder.toString().trim();
        String[] hexTokens = cleanText.split("\\s+");

        // Перевіряємо чи це схоже на HEX байти
        boolean looksLikeHex = true;
        for (String token : hexTokens) {
            if (!token.matches("[0-9A-Fa-f]{2}")) {
                looksLikeHex = false;
                break;
            }
        }

        if (looksLikeHex && hexTokens.length > 0) {
            try {
                byte[] hexBytes = new byte[hexTokens.length];
                for (int i = 0; i < hexTokens.length; i++) {
                    hexBytes[i] = (byte) Integer.parseInt(hexTokens[i], 16);
                }
                System.out.println("Parsed as HEX: " + hexBytes.length + " bytes");
                System.out.println("HEX tokens count: " + hexTokens.length);
                return hexBytes;
            } catch (Exception e) {
                System.out.println("HEX parse error: " + e.getMessage());
            }
        }

        // Якщо не HEX, то це текст
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        System.out.println("Parsed as text: " + textBytes.length + " bytes");
        return textBytes;
    }
}