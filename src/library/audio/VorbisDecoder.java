package library.audio;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;

import utils.IOUtils;

public class VorbisDecoder implements AudioDecoder {

	private IntBuffer channels = BufferUtils.createIntBuffer(1), sampleRate = BufferUtils.createIntBuffer(1);
	
	private IntBuffer error = BufferUtils.createIntBuffer(1);
	
	private String[] EXTENSIONS = { "ogg", "ogx" };
	
	@Override
	public ShortBuffer decodeFully(InputStream stream, AudioData data) {
		ByteBuffer memory = IOUtils.readBufferQuietly(stream);
		ShortBuffer pcm = STBVorbis.stb_vorbis_decode_memory(memory, channels, sampleRate);
		data.channels = channels.get(0);
		data.sampleRate = sampleRate.get(0);
		data.audioLengthSamples = -1;
		data.audioLengthSeconds = -1;
		return pcm;
	}

	@Override
	public AudioStream openStream(InputStream stream, AudioData data) {
		ByteBuffer memory = IOUtils.readBufferQuietly(stream);
		long handle = STBVorbis.stb_vorbis_open_memory(memory, error, null);
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			STBVorbis.stb_vorbis_get_info(handle, info);
			data.channels = info.channels();
			data.sampleRate = info.sample_rate();
			data.audioLengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(handle);
			data.audioLengthSeconds = STBVorbis.stb_vorbis_stream_length_in_seconds(handle);
		}
		return new VorbisStream(handle, stream, memory);
	}
	
	class VorbisStream implements AudioStream {
		
		private long handle;
		
		public InputStream stream;
		
		public ByteBuffer buffer;
		
		public VorbisStream(long handle, InputStream stream, ByteBuffer buffer) {
			this.handle = handle;
			this.stream = stream;
			this.buffer = buffer;
		}

		@Override
		public void reset() {
			STBVorbis.stb_vorbis_seek_start(handle);
		}

		@Override
		public int getSampleOffset() {
			return STBVorbis.stb_vorbis_get_sample_offset(handle);
		}

		@Override
		public void seek(int sampleNumber) {
			STBVorbis.stb_vorbis_seek(handle, sampleNumber);
		}

		@Override
		public int readSamples(ShortBuffer pcm, int channels) {
			final int bufferSize = pcm.capacity();
			int samples = 0;
			while (samples < bufferSize) {
				pcm.position(samples);
				int samplesPerChannel = STBVorbis.stb_vorbis_get_samples_short_interleaved(handle, channels, pcm);
				if (samplesPerChannel == 0) {
					break;
				}
				samples += samplesPerChannel * channels;
			}
			if (samples > 0) {
				pcm.position(0);
			}
			return samples;
		}
		
	}

	@Override
	public String[] getExtensions() {
		return EXTENSIONS;
	}

}
