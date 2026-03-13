package pl.krypto.des;

public class DES {
    private KeyGenerator keyGen;

    public DES(byte[] key) {
        this.keyGen = new KeyGenerator(key);
    }

    public byte[] encryptBlock(byte[] block) {
        if (block.length != 8) {
            throw new IllegalArgumentException("Block must be 8 bytes, got: " + block.length);
        }

        byte[] data = Permutation.permute(block, Permutation.IP, 64);

        for (int round = 0; round < 16; round++) {
            data = Round.process(data, keyGen.getRoundKey(round));
        }

        byte[] swapped = swapLR(data);
        return Permutation.permute(swapped, Permutation.FP, 64);
    }

    public byte[] decryptBlock(byte[] block) {
        if (block.length != 8) {
            throw new IllegalArgumentException("Block must be 8 bytes, got: " + block.length);
        }

        byte[] data = Permutation.permute(block, Permutation.IP, 64);

        for (int round = 15; round >= 0; round--) {
            data = Round.process(data, keyGen.getRoundKey(round));
        }

        byte[] swapped = swapLR(data);
        return Permutation.permute(swapped, Permutation.FP, 64);
    }

    private byte[] swapLR(byte[] data) {
        byte[] result = new byte[8];
        System.arraycopy(data, 4, result, 0, 4);
        System.arraycopy(data, 0, result, 4, 4);
        return result;
    }
}