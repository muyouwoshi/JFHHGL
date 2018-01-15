/**
 * 
 */
package com.juxin.predestinate.module.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;

/**
 * 基本类型和字节之间的互相转换。
 *
 * @author JohnsonLi
 * @version 1.0
 * @qq 505214658
 * @date 2015-04-16
 */
public class ByteUtil {
    // 返回1 byte
    public static byte[] toBytes(byte value) {
        byte[] b = new byte[1];
        b[0] = (byte) (value & 0xff);
        return b;
    }

	// 返回2 byte
	public static byte[] toBytes(short value) {
		byte[] b = new byte[2];
		b[1] = (byte) (value & 0xff);
		b[0] = (byte) (value >> 8 & 0xff);
		return b;
	}

	// 返回4 byte
	public static byte[] toBytes(int value) {
		byte[] b = new byte[4];
		b[3] = (byte) (value & 0xff);
		b[2] = (byte) (value >> 8 & 0xff);
		b[1] = (byte) (value >> 16 & 0xff);
		b[0] = (byte) (value >> 24 & 0xff);
		return b;
	}

    // 返回8 byte
    public static byte[] toBytes(long value) {
        byte[] b = new byte[8];
        b[7] = (byte) (value & 0xff);
        b[6] = (byte) (value >> 8 & 0xff);
        b[5] = (byte) (value >> 16 & 0xff);
        b[4] = (byte) (value >> 24 & 0xff);
        b[3] = (byte) (value >> 32 & 0xff);
        b[2] = (byte) (value >> 40 & 0xff);
        b[1] = (byte) (value >> 48 & 0xff);
        b[0] = (byte) (value >> 56 & 0xff);
        return b;
    }

	public static byte[] toBytes(String str) {
		byte[] retBytes = null;
		retBytes = str.getBytes(); // "GB2312"
		return retBytes;
	}
	
	// int表示unsigned short
	// 返回2 byte
	public static byte[] unsignedToBytes(int value) {
		byte[] b = new byte[2];
		b[1] = (byte) (value & 0xff);
		b[0] = (byte) (value >> 8 & 0xff);
		return b;
	}
	
	// long表示unsigned int
	// 返回4 byte
	public static byte[] unsignedToBytes(long value) {
		byte[] b = new byte[4];
		b[3] = (byte) (value & 0xff);
		b[2] = (byte) (value >> 8 & 0xff);
		b[1] = (byte) (value >> 16 & 0xff);
		b[0] = (byte) (value >> 24 & 0xff);
		return b;
	}

	public static byte[] toUnsignedBytes(byte[] signChar) {
		for (int i = 0; i < signChar.length; i++) {
			int x = signChar[i] >= 0 ? signChar[i] : signChar[i] + 256;
			signChar[i] = (byte) x;
		}
		
		return signChar;
	}

    // 读取1 byte
    public static byte toByte(byte[] buffer) {
        byte n = (byte) (buffer[0] & 0xff);
        return n;
    }

    // 读取1 byte
    public static short toUnsignedByte(byte[] buffer) {
        short n = (short) (buffer[0] & 0xff);
        return n;
    }

	
	// 读取2 byte
	public static short toShort(byte[] buffer) {
		short n = (short) ((buffer[0] & 0xff) << 8);
		n = (short) (n | (short) ((buffer[1] & 0xff) << 0));
		return n;
	}
	
	// 读取2 byte
	public static int toUnsignedShort(byte[] buffer) {
		int n = (buffer[0] & 0xff) << 8;
		n = n | (buffer[1] & 0xff) << 0;
		return n;
	}

	// 读取4 byte
	public static int toInt(byte[] buffer) {
		int n = (buffer[0] & 0xff) << 24;
		n = n | (buffer[1] & 0xff) << 16;
		n = n | (buffer[2] & 0xff) << 8;
		n = n | (buffer[3] & 0xff) << 0;
		return n;
	}
	
	// 读取4 byte
	public static long toUnsignedInt(byte[] buffer) {
		long n = (long) ((buffer[0] & 0xff) << 24);
		n = n | (long) ((buffer[1] & 0xff) << 16);
		n = n | (long) ((buffer[2] & 0xff) << 8);
		n = n | (long) (buffer[3] & 0xff) << 0;
		return n;
	}

    // 读取8 byte
    public static long toLong(byte[] buffer) {
        long n = (long) ((buffer[0] & 0xff) << 56);
        n = n | (long) ((buffer[1] & 0xff) << 48);
        n = n | (long) ((buffer[2] & 0xff) << 40);
        n = n | (long) (buffer[3] & 0xff) << 32;
        n = n | (long) ((buffer[4] & 0xff) << 24);
        n = n | (long) ((buffer[5] & 0xff) << 16);
        n = n | (long) (buffer[6] & 0xff) << 8;
        n = n | (long) (buffer[7] & 0xff) << 0;
        return n;
    }

	public static String toString(byte[] b) {
		String str = "";
		str = new String(b); // "GB2312"
		return str;
	}

	/**
	 * 转成utf-8的byte[]
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] toBytesUTF(String str) throws UnsupportedEncodingException {
		byte[] strByte = new byte[0];
		if(!TextUtils.isEmpty(str)){
			strByte = str.getBytes("UTF-8");
		}

		return strByte;
	}
}
