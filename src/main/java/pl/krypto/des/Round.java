package pl.krypto.des;

public class Round {

    /**
     * Виконує один раунд DES
     * @param data 64 біти (8 байт) - вхідні дані
     * @param roundKey 48 біт (6 байт) - ключ раунду
     * @return 64 біти (8 байт) після раунду
     */
    public static byte[] process(byte[] data, byte[] roundKey) {
        // Розбиваємо 64 біти на L і R (по 32 біти)
        byte[] L = new byte[4];
        byte[] R = new byte[4];

        // Копіюємо ліву і праву половини
        System.arraycopy(data, 0, L, 0, 4);
        System.arraycopy(data, 4, R, 0, 4);

        // Функція F: обробляємо праву половину
        byte[] newR = f(R, roundKey);

        // newR XOR з L
        byte[] newL = xor(L, newR);

        // Результат: R (стара права половина) + newL
        byte[] result = new byte[8];
        System.arraycopy(R, 0, result, 0, 4);      // Стара R стає новою L
        System.arraycopy(newL, 0, result, 4, 4);   // newL стає новою R

        return result;
    }

    /**
     * Функція F DES: розширення -> XOR з ключем -> S-box -> перестановка P
     */
    private static byte[] f(byte[] R, byte[] roundKey) {
        // 1. Розширення R з 32 до 48 біт (таблиця E)
        byte[] expandedR = Permutation.permute(R, Permutation.E, 48);

        // 2. XOR з раундовим ключем
        byte[] xored = xor(expandedR, roundKey);

        // 3. S-бокси (48 біт -> 32 біти)
        byte[] sBoxOutput = SBox.substitute(xored);

        // 4. Перестановка P (32 біти)
        return Permutation.permute(sBoxOutput, Permutation.P, 32);
    }

    /**
     * XOR двох масивів байтів (побайтово)
     */
    private static byte[] xor(byte[] a, byte[] b) {
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