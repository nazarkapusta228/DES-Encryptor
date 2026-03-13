package pl.krypto.gui;

import javax.swing.*;
import java.io.File;

public class FileChooserHandler {
    private JFileChooser fileChooser;

    public FileChooserHandler() {
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
    }

    public File openFileChooser() {
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public File saveFileChooser() {
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
}