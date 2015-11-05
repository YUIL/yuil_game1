package com.yuil.game.util;

/**
 * @author dj-004
 * @changedby dj-004
 */
public class DataUtil {
	/**
	 * @param src
	 *            {byte[]:{"length":4}}
	 * @return {int}
	 */
	public static int bytesToInt(byte[] src) {
		int value = 0;
		for (int i = 0; i < src.length; i++) {
			value = value | ((src[i] & 0xFF) << (i * 8));
		}
		return value;
	}

	/**
	 * @param src
	 *            {int}
	 * @return {byte[]:{"length":4}}
	 */
	public static byte[] intToBytes(int src) {

		byte[] bytes = new byte[4];
		for (int b = 0; b < bytes.length; b++) {
			bytes[b] = (byte) (src >> (b * 8));
		}
		return bytes;
	}

	public static void subByte(byte[] src, byte[] dst, int offset) {
		for (int i = 0; i < dst.length; i++) {
			dst[i] = src[i + offset];
		}
	}

	public static byte[] subByte(byte[] src, int length, int offset) {
		byte[] dst = new byte[length];
		for (int i = 0; i < dst.length; i++) {
			dst[i] = src[i + offset];
		}
		return dst;
	}

	public static byte[] longToBytes(long num) {
		byte[] byteNum = new byte[8];
		for (int ix = 0; ix < 8; ++ix) {
			int offset = 64 - (ix + 1) * 8;
			byteNum[ix] = (byte) ((num >> offset) & 0xff);
		}
		return byteNum;
	}

	public static long bytesToLong(byte[] byteNum) {
		long num = 0;
		for (int ix = 0; ix < 8; ++ix) {
			num <<= 8;
			num |= (byteNum[ix] & 0xff);
		}
		return num;
	}

	public static byte[] floatToBytes(float f) {

		// 把float转换为byte[]
		int fbit = Float.floatToIntBits(f);

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (fbit >> (24 - i * 8));
		}

		// 翻转数组
		int len = b.length;
		// 建立一个与源数组元素类型相同的数组
		byte[] dest = new byte[len];
		// 为了防止修改源数组，将源数组拷贝一份副本
		System.arraycopy(b, 0, dest, 0, len);
		byte temp;
		// 将顺位第i个与倒数第i个交换
		for (int i = 0; i < len / 2; ++i) {
			temp = dest[i];
			dest[i] = dest[len - i - 1];
			dest[len - i - 1] = temp;
		}

		return dest;

	}

	public static float bytesToFloat(byte[] b) {
		int l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		return Float.intBitsToFloat(l);
	}

	public static byte[] shortToBytes(short s) {
		byte[] dest = new byte[2];
		dest[1] = (byte) (s >> 8);
		dest[0] = (byte) (s >> 0);
		return dest;
	}

	public static short bytesToShort(byte[] src) {
		return (short) (((src[1] << 8) | src[0] & 0xff));
	}

	public static int getUnsignedNum(byte data) { // 将data字节型数据转换为0~255 (0xFF
													// 即BYTE)。
		return data & 0x0FF;
	}

	public static int getUnsignedNum(short data) { // 将data字节型数据转换为0~65535
													// (0xFFFF 即 WORD)。
		return data & 0x0FFFF;
	}

	public static long getUnsignedNum(int data) { // 将int数据转换为0~4294967295
													// (0xFFFFFFFF即DWORD)。
		return data & 0x0FFFFFFFFl;
	}

}