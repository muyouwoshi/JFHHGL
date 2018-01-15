package com.juxin.library.utils;

public class JniUtil {
    static {
        System.loadLibrary("JniDes");
    }

    public static native String GetEncryptString(String data);

    public static native byte[] GetDecryptString(String data);
}
