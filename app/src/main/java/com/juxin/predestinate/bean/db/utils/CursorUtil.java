package com.juxin.predestinate.bean.db.utils;

import android.database.Cursor;

import java.io.UnsupportedEncodingException;

/**
 * Created by Kind on 2017/3/28.
 */

public class CursorUtil {

    private CursorUtil() {
    }

    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    public static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    public static double getDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(columnName));
    }

    public static float getFloat(Cursor cursor, String columnName) {
        return cursor.getFloat(cursor.getColumnIndexOrThrow(columnName));
    }

    public static short getShort(Cursor cursor, String columnName) {
        return cursor.getShort(cursor.getColumnIndexOrThrow(columnName));
    }

    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    public static byte[] getBlob(Cursor cursor, String columnName) {
        return cursor.getBlob(cursor.getColumnIndexOrThrow(columnName));
    }

    public static String getBlobToString(Cursor cursor, String columnName) {
        if (cursor == null || columnName == null) {
            return "";
        }
        try {
            byte[] bytes = getBlob(cursor, columnName);
            if (bytes == null || bytes.length < 0) {
                return "";
            }
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            //oom
            return "";
        } catch (Exception e){
            e.printStackTrace();
            // all exceptions
            return "";
        }
    }
}
