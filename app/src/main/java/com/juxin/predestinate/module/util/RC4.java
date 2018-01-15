package com.juxin.predestinate.module.util;

/**
 * Created by siow on 2016/3/25.
 */

public class RC4 {

    private static void swap(byte[] ary, int idx1, int idx2) {
        byte temp = ary[idx1];
        ary[idx1] = ary[idx2];
        ary[idx2] = temp;
    }

    private static byte[] initKeyBox(byte[] aryKey) {
        byte[] keyBox = new byte[256];

        for (int i = 0; i < 256; i++)
            keyBox[i] = (byte) i;

        int j = 0;

        for (int i = 0; i < 256; i++) {
            j = (j + (keyBox[i] & 0xFF) + (aryKey[i % aryKey.length] & 0xFF)) & 0xFF;
            swap(keyBox, i, j);
        }
        return keyBox;
    }

    public static String CryptRc4(byte[] aryInput, byte[] aryKey, String charsetName) {
        try {
            byte[] keyBox = initKeyBox(aryKey);

            int i = 0;
            int j = 0;

            byte[] aryBts = new byte[aryInput.length];

            for (int k = 0; k < aryInput.length; k++) {
                i = (i + 1) & 0xFF;
                j = (j + (keyBox[i] & 0xFF)) & 0xFF;

                swap(keyBox, i, j);

                int t = ((keyBox[i] & 0xFF) + (keyBox[j] & 0xFF)) & 0xFF;
                aryBts[k] = (byte) (aryInput[k] ^ keyBox[t]);
            }

            return new String(aryBts, charsetName);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String CryptRc4(byte[] aryInput, byte[] aryKey) {
        return CryptRc4(aryInput, aryKey, "UTF-8");
    }
}
