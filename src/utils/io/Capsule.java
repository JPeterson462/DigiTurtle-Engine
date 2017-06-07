package utils.io;

import java.io.InputStream;
import java.io.OutputStream;

public class Capsule implements Readable, Writable {
	
	public static final int BUFFER_SIZE = 1024;
	
	public static final int BYTE_SIZE = Byte.BYTES;
	
	public static final int CHAR_SIZE = Character.BYTES;
	
	public static final int SHORT_SIZE = Short.BYTES;
	
	public static final int INT_SIZE = Integer.BYTES;
	
	public static final int FLOAT_SIZE = Float.BYTES;
	
	public static final int LONG_SIZE = Long.BYTES;
	
	public static final int DOUBLE_SIZE = Double.BYTES;
	
	private CapsuleMode mode = CapsuleMode.WRITE;
	
	private WriteCapsule writeCapsule = new WriteCapsule();
	
	private ReadCapsule readCapsule = new ReadCapsule();
	
	public void setReadMode(InputStream stream) {
		mode = CapsuleMode.READ;
		readCapsule.setSource(stream);
	}
	
	public void setWriteMode(OutputStream stream) {
		mode = CapsuleMode.WRITE;
		writeCapsule.setSource(stream);
	}
	
	public CapsuleMode getMode() {
		return mode;
	}
	
	public void flush() {
		if (!mode.equals(CapsuleMode.WRITE)) {
			throw new IllegalStateException("Capsule not ready for writing");
		}
		writeCapsule.flush();
	}

	@Override
	public byte readByte() {
		if (!mode.equals(CapsuleMode.READ)) {
			throw new IllegalStateException("Capsule not ready for reading");
		}
		return readCapsule.readByte();
	}

	@Override
	public char readChar() {
		if (!mode.equals(CapsuleMode.READ)) {
			throw new IllegalStateException("Capsule not ready for reading");
		}
		return readCapsule.readChar();
	}

	@Override
	public short readShort() {
		if (!mode.equals(CapsuleMode.READ)) {
			throw new IllegalStateException("Capsule not ready for reading");
		}
		return readCapsule.readShort();
	}

	@Override
	public int readInt() {
		if (!mode.equals(CapsuleMode.READ)) {
			throw new IllegalStateException("Capsule not ready for reading");
		}
		return readCapsule.readInt();
	}

	@Override
	public float readFloat() {
		if (!mode.equals(CapsuleMode.READ)) {
			throw new IllegalStateException("Capsule not ready for reading");
		}
		return readCapsule.readFloat();
	}

	@Override
	public long readLong() {
		if (!mode.equals(CapsuleMode.READ)) {
			throw new IllegalStateException("Capsule not ready for reading");
		}
		return readCapsule.readLong();
	}

	@Override
	public double readDouble() {
		if (!mode.equals(CapsuleMode.READ)) {
			throw new IllegalStateException("Capsule not ready for reading");
		}
		return readCapsule.readDouble();
	}

	@Override
	public String readString() {
		if (!mode.equals(CapsuleMode.READ)) {
			throw new IllegalStateException("Capsule not ready for reading");
		}
		return readCapsule.readString();
	}

	@Override
	public void writeByte(byte b) {
		if (!mode.equals(CapsuleMode.WRITE)) {
			throw new IllegalStateException("Capsule not ready for writing");
		}
		writeCapsule.writeByte(b);
	}

	@Override
	public void writeChar(char c) {
		if (!mode.equals(CapsuleMode.WRITE)) {
			throw new IllegalStateException("Capsule not ready for writing");
		}
		writeCapsule.writeChar(c);
	}

	@Override
	public void writeShort(short s) {
		if (!mode.equals(CapsuleMode.WRITE)) {
			throw new IllegalStateException("Capsule not ready for writing");
		}
		writeCapsule.writeShort(s);
	}

	@Override
	public void writeInt(int i) {
		if (!mode.equals(CapsuleMode.WRITE)) {
			throw new IllegalStateException("Capsule not ready for writing");
		}
		writeCapsule.writeInt(i);
	}

	@Override
	public void writeFloat(float f) {
		if (!mode.equals(CapsuleMode.WRITE)) {
			throw new IllegalStateException("Capsule not ready for writing");
		}
		writeCapsule.writeFloat(f);
	}

	@Override
	public void writeLong(long l) {
		if (!mode.equals(CapsuleMode.WRITE)) {
			throw new IllegalStateException("Capsule not ready for writing");
		}
		writeCapsule.writeLong(l);
	}

	@Override
	public void writeDouble(double d) {
		if (!mode.equals(CapsuleMode.WRITE)) {
			throw new IllegalStateException("Capsule not ready for writing");
		}
		writeCapsule.writeDouble(d);
	}

	@Override
	public void writeString(String string) {
		if (!mode.equals(CapsuleMode.WRITE)) {
			throw new IllegalStateException("Capsule not ready for writing");
		}
		writeCapsule.writeString(string);
	}

}
