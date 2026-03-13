package pl.krypto.utils;

public class ByteUtils {

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02X", bytes[i]));
            if (i < bytes.length - 1) {
                sb.append(" ");
            }
            // Додаємо перенесення рядка кожні 16 байт для кращої читабельності
            if ((i + 1) % 8 == 0 && i < bytes.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new byte[0];
        }

        // Видаляємо всі пробіли, перенесення рядків і табуляції
        String clean = hex.replaceAll("\\s+", "");

        // Перевіряємо чи це взагалі HEX
        if (!clean.matches("[0-9A-Fa-f]+")) {
            throw new IllegalArgumentException("Invalid hex string: contains non-hex characters");
        }

        // Якщо довжина непарна, додаємо 0 на початок
        if (clean.length() % 2 != 0) {
            clean = "0" + clean;
        }

        int len = clean.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(clean.charAt(i), 16) << 4)
                    + Character.digit(clean.charAt(i + 1), 16));
        }

        return data;
    }

    public static byte[] xor(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }
}