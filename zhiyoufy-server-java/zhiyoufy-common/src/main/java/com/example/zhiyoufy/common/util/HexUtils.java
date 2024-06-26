package com.example.zhiyoufy.common.util;

import com.example.zhiyoufy.common.internal.RecyclableBuffers;

/**
 * hex工具类
 */
public class HexUtils {
	public static String toLowerHex(long high, long low) {
		if (high != 0) {
			char[] result = RecyclableBuffers.parseBuffer();
			writeHexLong(result, 0, high);
			writeHexLong(result, 16, low);
			return new String(result, 0, 32);
		}
		return toLowerHex(low);
	}

	public static String toLowerHex(long v) {
		char[] data = RecyclableBuffers.parseBuffer();
		writeHexLong(data, 0, v);
		return new String(data, 0, 16);
	}

	public static void writeHexLong(char[] data, int pos, long v) {
		writeHexByte(data, pos + 0, (byte) ((v >>> 56L) & 0xff));
		writeHexByte(data, pos + 2, (byte) ((v >>> 48L) & 0xff));
		writeHexByte(data, pos + 4, (byte) ((v >>> 40L) & 0xff));
		writeHexByte(data, pos + 6, (byte) ((v >>> 32L) & 0xff));
		writeHexByte(data, pos + 8, (byte) ((v >>> 24L) & 0xff));
		writeHexByte(data, pos + 10, (byte) ((v >>> 16L) & 0xff));
		writeHexByte(data, pos + 12, (byte) ((v >>> 8L) & 0xff));
		writeHexByte(data, pos + 14, (byte) (v & 0xff));
	}

	static final char[] HEX_DIGITS =
			{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	static void writeHexByte(char[] data, int pos, byte b) {
		data[pos + 0] = HEX_DIGITS[(b >> 4) & 0xf];
		data[pos + 1] = HEX_DIGITS[b & 0xf];
	}
}