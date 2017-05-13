package library.openal;

import java.nio.FloatBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.SOFTDirectChannels;

public class ALSource implements ALResource {
	
	private FloatBuffer vec3 = BufferUtils.createFloatBuffer(3);
	
	private int id;
	
	public ALSource() {
		id = AL10.alGenSources();
		vec3.limit(vec3.capacity());
		if (AL.getCapabilities().AL_SOFT_direct_channels) {
			AL10.alSourcei(id, SOFTDirectChannels.AL_DIRECT_CHANNELS_SOFT, AL10.AL_TRUE);
		}
	}
	
	public void attachBuffer(ALBuffer buffer) {
		AL10.alSourcei(id, AL10.AL_BUFFER, buffer.getID());
	}
	
	public void setLooping(boolean looping) {
		AL10.alSourcei(id, AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
	}
	
	public void setPosition(float x, float y, float z) {
		vec3.put(0, x);
		vec3.put(1, y);
		vec3.put(2, z);
		AL10.alSourcefv(id, AL10.AL_POSITION, vec3);
	}
	
	public void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}
	
	public void setVelocity(float vx, float vy, float vz) {
		vec3.put(0, vx);
		vec3.put(1, vy);
		vec3.put(2, vz);
		AL10.alSourcefv(id, AL10.AL_VELOCITY, vec3);
	}
	
	public void setVelocity(Vector3f velocity) {
		setVelocity(velocity.x, velocity.y, velocity.z);
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void delete() {
		AL10.alDeleteSources(id);
	}

	public int hashCode() {
		return Integer.hashCode(id);
	}
	
	public boolean equals(Object object) {
		return object instanceof ALSource && ((ALSource) object).id == id;
	}

}
