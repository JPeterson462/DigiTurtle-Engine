package library.openal;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.lwjgl.system.MemoryUtil;

public class ALContext {
	
	private long device, context;
	
	public ALContext() {
		device = ALC10.alcOpenDevice((ByteBuffer) null);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		context = ALC10.alcCreateContext(device, (IntBuffer) null);
		EXTThreadLocalContext.alcSetThreadContext(context);
		AL.createCapabilities(deviceCaps);
	}
	
	public void delete() {
		EXTThreadLocalContext.alcSetThreadContext(MemoryUtil.NULL);
	}
	
	public int hashCode() {
		int hashCode = 0;
		hashCode = 31 * hashCode + Long.hashCode(device);
		hashCode = 31 * hashCode + Long.hashCode(context);
		return hashCode;
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof ALContext)) {
			return false;
		}
		ALContext context = (ALContext) object;
		return device == context.device && this.context == context.context;
	}

}
