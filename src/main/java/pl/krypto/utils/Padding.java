package pl.krypto.utils;

public class Padding {
    private static final int BLOCK_SIZE = 8;

    public static byte[] addPadding(byte[] data) {
        int paddingLength = BLOCK_SIZE - (data.length % BLOCK_SIZE);

        // Якщо дані вже кратні 8, додаємо цілий блок
        if (paddingLength == 0) {
            paddingLength = BLOCK_SIZE;
        }

        byte[] padded = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, padded, 0, data.length);

        // PKCS5Padding
        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) paddingLength;
        }

        return padded;
    }

    public static byte[] removePadding(byte[] data) {
        if (data.length == 0 || data.length % BLOCK_SIZE != 0) {
            return data;
        }

        int paddingLength = data[data.length - 1] & 0xFF;

        if (paddingLength < 1 || paddingLength > BLOCK_SIZE) {
            return data;
        }

        // Перевіряємо padding
        for (int i = data.length - paddingLength; i < data.length; i++) {
            if ((data[i] & 0xFF) != paddingLength) {
                return data;
            }
        }

        byte[] unpadded = new byte[data.length - paddingLength];
        System.arraycopy(data, 0, unpadded, 0, unpadded.length);
        return unpadded;
    }
}