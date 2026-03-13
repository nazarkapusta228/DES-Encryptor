package pl.krypto.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler {

    public static byte[] readFile(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public static void writeFile(File file, byte[] data) throws IOException {
        Files.write(file.toPath(), data);
    }

    public static String readTextFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), "UTF-8");
    }

    public static void writeTextFile(File file, String text) throws IOException {
        Files.write(file.toPath(), text.getBytes("UTF-8"));
    }
}