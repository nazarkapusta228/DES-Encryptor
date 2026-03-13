package pl.krypto;

import pl.krypto.gui.MainWindow;
import pl.krypto.utils.Padding;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Тест padding
        testPadding();

        // Запуск GUI
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainWindow().setVisible(true);
        });
    }

    private static void testPadding() {
        System.out.println("=== Testing Padding ===");

        byte[] test1 = "Hello".getBytes();
        byte[] padded1 = Padding.addPadding(test1);
        System.out.println("Hello (5 bytes) -> padded: " + padded1.length + " bytes");

        byte[] test2 = "12345678".getBytes();
        byte[] padded2 = Padding.addPadding(test2);
        System.out.println("12345678 (8 bytes) -> padded: " + padded2.length + " bytes");

        byte[] test3 = new byte[21];
        byte[] padded3 = Padding.addPadding(test3);
        System.out.println("21 bytes -> padded: " + padded3.length + " bytes (maє buty 24)");
    }
}