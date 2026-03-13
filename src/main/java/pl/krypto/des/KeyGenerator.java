package pl.krypto.des;

public class KeyGenerator {
    private static final int[] PC1 = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    private static final int[] PC2 = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };

    private static final int[] SHIFT_SCHEDULE = {
            1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };

    private byte[][] roundKeys = new byte[16][6];

    public KeyGenerator(byte[] key) {
        if (key.length != 8) {
            throw new IllegalArgumentException("DES key must be 8 bytes");
        }
        generateRoundKeys(key);
    }

    private void generateRoundKeys(byte[] key) {
        byte[] permutedKey = Permutation.permute(key, PC1, 56);

        byte[] C = new byte[4];
        byte[] D = new byte[4];
        splitCD(permutedKey, C, D);

        for (int round = 0; round < 16; round++) {
            shiftLeft(C, SHIFT_SCHEDULE[round]);
            shiftLeft(D, SHIFT_SCHEDULE[round]);

            byte[] combined = combineCD(C, D);
            roundKeys[round] = Permutation.permute(combined, PC2, 48);
        }
    }

    private void splitCD(byte[] permutedKey, byte[] C, byte[] D) {
        for (int i = 0; i < 28; i++) {
            int byteIndex = i / 8;
            int bitIndex = 7 - (i % 8);

            int bit = (permutedKey[byteIndex] >> bitIndex) & 1;
            if (bit == 1) {
                C[byteIndex] |= (1 << bitIndex);
            }

            int dBitPos = i + 28;
            int dByteIndex = dBitPos / 8;
            int dBitIndex = 7 - (dBitPos % 8);
            bit = (permutedKey[dByteIndex] >> dBitIndex) & 1;
            if (bit == 1) {
                D[byteIndex] |= (1 << bitIndex);
            }
        }
    }

    private void shiftLeft(byte[] keyPart, int shifts) {
        for (int s = 0; s < shifts; s++) {
            int carry = (keyPart[0] >> 7) & 1;

            for (int i = 0; i < 3; i++) {
                keyPart[i] = (byte) ((keyPart[i] << 1) | ((keyPart[i + 1] >> 7) & 1));
            }
            keyPart[3] = (byte) ((keyPart[3] << 1) | carry);
            keyPart[3] &= 0xF0;
        }
    }

    private byte[] combineCD(byte[] C, byte[] D) {
        byte[] result = new byte[7];

        for (int i = 0; i < 3; i++) {
            result[i] = C[i];
        }
        result[3] = (byte) ((C[3] & 0xF0) | ((D[0] >> 4) & 0x0F));

        for (int i = 1; i < 4; i++) {
            result[i + 3] = D[i];
        }

        return result;
    }

    public byte[] getRoundKey(int round) {
        return roundKeys[round];
    }
}