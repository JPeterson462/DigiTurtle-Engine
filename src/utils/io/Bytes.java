package utils.io;

public class Bytes {

	public static byte getByte(byte[] buffer, int position) {
		return buffer[position];
	}
	
	public static void putByte(byte[] buffer, int position, byte b) {
		buffer[position] = b;
	}

	public static char getChar(byte[] buffer, int position) {
		return (char) (((buffer[position] & 0xFF) << 8) | 
				(buffer[position + 1] & 0xFF));
	}
	
	public static void putChar(byte[] buffer, int position, char c) {
		byte b0 = (byte) ((c >> 8) & 0xFF);
		byte b1 = (byte) (c & 0xFF);
		buffer[position] = b0;
		buffer[position + 1] = b1;
	}

	public static short getShort(byte[] buffer, int position) {
		return (short) (((buffer[position] & 0xFF) << 8) | 
				(buffer[position + 1] & 0xFF));
	}
	
	public static void putShort(byte[] buffer, int position, short s) {
		byte b0 = (byte) ((s >> 8) & 0xFF);
		byte b1 = (byte) (s & 0xFF);
		buffer[position] = b0;
		buffer[position + 1] = b1;
	}

	public static int getInt(byte[] buffer, int position) {
		return ((buffer[position] & 0xFF) << 24) | 
				((buffer[position + 1] & 0xFF) << 16) | 
				((buffer[position + 2] & 0xFF) << 8) | 
				(buffer[position + 3] & 0xFF);
	}
	
	public static void putInt(byte[] buffer, int position, int i) {
		byte b0 = (byte) ((i >> 24) & 0xFF);
		byte b1 = (byte) ((i >> 16) & 0xFF);
		byte b2 = (byte) ((i >> 8) & 0xFF);
		byte b3 = (byte) (i & 0xFF);
		buffer[position] = b0;
		buffer[position + 1] = b1;
		buffer[position + 2] = b2;
		buffer[position + 3] = b3;
	}

	public static float getFloat(byte[] buffer, int position) {
		int i = getInt(buffer, position);
		return Float.intBitsToFloat(i);
	}
	
	public static void putFloat(byte[] buffer, int position, float f) {
		putInt(buffer, position, Float.floatToIntBits(f));
	}

	public static long getLong(byte[] buffer, int position) {
		return ((buffer[position] & 0xFF) << 56) | 
				((buffer[position + 1] & 0xFF) << 48) | 
				((buffer[position + 2] & 0xFF) << 40) | 
				((buffer[position + 3] & 0xFF) << 32) | 
				((buffer[position + 4] & 0xFF) << 24) | 
				((buffer[position + 5] & 0xFF) << 16) | 
				((buffer[position + 6] & 0xFF) << 8) | 
				(buffer[position + 7] & 0xFF);
	}
	
	public static void putLong(byte[] buffer, int position, long l) {
		byte b0 = (byte) ((l >> 56) & 0xFF);
		byte b1 = (byte) ((l >> 48) & 0xFF);
		byte b2 = (byte) ((l >> 40) & 0xFF);
		byte b3 = (byte) ((l >> 32) & 0xFF);
		byte b4 = (byte) ((l >> 24) & 0xFF);
		byte b5 = (byte) ((l >> 16) & 0xFF);
		byte b6 = (byte) ((l >> 8) & 0xFF);
		byte b7 = (byte) (l & 0xFF);
		buffer[position] = b0;
		buffer[position + 1] = b1;
		buffer[position + 2] = b2;
		buffer[position + 3] = b3;
		buffer[position + 4] = b4;
		buffer[position + 5] = b5;
		buffer[position + 6] = b6;
		buffer[position + 7] = b7;
	}
	
	public static double getDouble(byte[] buffer, int position) {
		long l = getLong(buffer, position);
		return Double.longBitsToDouble(l);
	}
	
	public static void putDouble(byte[] buffer, int position, double d) {
		putLong(buffer, position, Double.doubleToLongBits(d));
	}

}
