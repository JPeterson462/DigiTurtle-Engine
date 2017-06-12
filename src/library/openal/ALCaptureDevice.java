package library.openal;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.system.MemoryUtil;

public class ALCaptureDevice {
	
	public static final int DEFAULT_FREQUENCY = 44100;
	
	public static final int DEFAULT_BUFFER_SIZE = 1024;
	
	private long device;
	
	private int format, bufferSize;
	
	public ALCaptureDevice(int format) {
		this(DEFAULT_FREQUENCY, format, DEFAULT_BUFFER_SIZE);
	}

	public ALCaptureDevice(int frequency, int format, int bufferSize) {
		if (!AL.getCapabilities().OpenAL11) {
			throw new IllegalStateException("Device not capable of recording audio");
		}
		device = ALC11.alcCaptureOpenDevice((ByteBuffer) null, frequency, format, bufferSize);
		if (device == MemoryUtil.NULL) {
			throw new IllegalStateException("Device not capable of recording audio");
		}
		this.format = format;
		this.bufferSize = bufferSize;
	}
	
//	private int transformSamples(int samples) {
//		if (format == AL10.AL_FORMAT_STEREO16 || format == AL10.AL_FORMAT_STEREO8) {
//			return samples / 2;
//		}
//		return samples;
//	}
	
	public ShortBuffer sampleDevice() {
		ALC11.alcCaptureStart(device);
		int samples = ALC10.alcGetInteger(device, ALC11.ALC_CAPTURE_SAMPLES);
		if (samples == 0) {
			return null;
		}
		ALC11.alcCaptureStop(device);
		ShortBuffer pcm = BufferUtils.createShortBuffer(bufferSize);
		ALC11.alcCaptureSamples(device, pcm, bufferSize);
		return pcm;
	}
	
	public void close() {
		ALC11.alcCaptureCloseDevice(device);
	}
	
	public int hashCode() {
		return Long.hashCode(device);
	}
	
	public boolean equals(Object object) {
		return object instanceof ALCaptureDevice && ((ALCaptureDevice) object).device == device;
	}
	
}
