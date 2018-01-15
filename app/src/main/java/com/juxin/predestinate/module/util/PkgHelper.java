package com.juxin.predestinate.module.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by siow on 2016/3/25.
 */

public final class PkgHelper {
    private static final int SHORT_LENGTH = 2;
    private static final byte[] MAGIC = new byte[]{0x21, 0x4a, 0x58, 0x64, 0x21}; //!JXd!
    private static String sMarket;
    private static String sMainChannel;
    private static String sSubChannel;

    public static synchronized String getMarket(final Context context, final String defaultValue) {
        if (sMarket == null)
            getMarketInfo(context);

        return TextUtils.isEmpty(sMarket) ? defaultValue : sMarket;
    }

    public static synchronized String getMainChannel(final Context context, final String defaultValue) {
        if (sMainChannel == null)
            getMarketInfo(context);

        return TextUtils.isEmpty(sMainChannel) ? defaultValue : sMainChannel;
    }

    public static synchronized String getSubChannel(final Context context, final String defaultValue) {
        if (sSubChannel == null)
            getMarketInfo(context);

        return TextUtils.isEmpty(sSubChannel) ? defaultValue : sSubChannel;
    }

    private static void getMarketInfo(final Context context) {
        if (sMarket == null) {
            sMarket = "";
            sMainChannel = "";
            sSubChannel = "";
            try {
                byte[] arySrc = readZipComment(new File(getSourceDir(context)));
                sMarket = RC4.CryptRc4(arySrc, MAGIC);
                Log.i("aaa", sMarket);

                int iL = sMarket.split("_").length;
                if (iL >= 3) {
                    sMainChannel = sMarket.split("_")[iL - 2];
                    sSubChannel = sMarket.split("_")[iL - 1];
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    private static String getSourceDir(final Context context) {
        String sourceDir = context.getApplicationInfo().publicSourceDir;
        if (TextUtils.isEmpty(sourceDir)) {
            sourceDir = context.getApplicationInfo().sourceDir;
        }
        if (TextUtils.isEmpty(sourceDir)) {
            sourceDir = context.getPackageCodePath();
        }
        return sourceDir;
    }

    private static boolean isMagicMatched(byte[] buffer) {
        if (buffer.length != MAGIC.length) {
            return false;
        }
        for (int i = 0; i < MAGIC.length; ++i) {
            if (buffer[i] != MAGIC[i]) {
                return false;
            }
        }
        return true;
    }

    private static short readShort(DataInput input) throws IOException {
        byte[] buf = new byte[SHORT_LENGTH];
        input.readFully(buf);
        ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort(0);
    }

    private static byte[] readZipComment(File file) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            long index = raf.length();
            byte[] buffer = new byte[MAGIC.length];
            index -= MAGIC.length;
            // read magic bytes
            raf.seek(index);
            raf.readFully(buffer);
            // if magic bytes matched
            if (isMagicMatched(buffer)) {
                index -= SHORT_LENGTH;
                raf.seek(index);
                // read content length field
                int length = readShort(raf);
                if (length > 0) {
                    Log.i("aaa", "readZipComment length:" + length);
                    index -= length;
                    raf.seek(index);
                    // read content bytes
                    byte[] bytesComment = new byte[length];
                    raf.readFully(bytesComment);

                    return bytesComment;
                } else {
                    throw new IOException("zip comment content not found");
                }
            } else {
                throw new IOException("zip comment magic bytes not found");
            }
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
    }
}
