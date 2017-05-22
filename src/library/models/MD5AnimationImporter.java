package library.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.esotericsoftware.minlog.Log;

import utils.AtomicObject;

public class MD5AnimationImporter implements AnimationImporter {

	private String[] EXTENSIONS = { "md5anim" };
	
	class MD5Joint {
		int parent;
		String name;
		int flags;
		int dataOffset;
	}
	
	class MD5JointData {
		Vector3f position;
		Vector3f orientation;
	}
	
	@Override
	public Animation importAnimation(InputStream stream, String animation) {
		final int DEFAULT_SECTION = 0, HIERARCHY_SECTION = 1, BOUNDS_SECTION = 2, BASEFRAME_SECTION = 3, FRAME_SECTION = 4;
		AtomicInteger section = new AtomicInteger(DEFAULT_SECTION);
		AtomicInteger frame = new AtomicInteger();
		AtomicObject<Float> delta = new AtomicObject<>();
		ArrayList<Float> frameData = new ArrayList<>();
		ArrayList<MD5Joint> joints = new ArrayList<>();
		ArrayList<MD5JointData> jointData = new ArrayList<>();
		ArrayList<KeyFrameData> keyframeData = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			reader.lines().forEach(line -> {
				line = line.trim();
				String[] parts = line.split("[\\s]+");
				if (section.get() == DEFAULT_SECTION) {
					if (line.startsWith("MD5Version ")) {
						if (!parts[1].equalsIgnoreCase("10")) {
							Log.error("Invalid Version");
						}
					}
					else if (line.startsWith("frameRate")) {
						delta.set(1f / (float) Integer.parseInt(parts[1]));
					}
					else if (line.startsWith("hierarchy {")) {
						section.set(HIERARCHY_SECTION);
					}
					else if (line.startsWith("bounds {")) {
						section.set(BOUNDS_SECTION);
					}
					else if (line.startsWith("baseframe {")) {
						section.set(BASEFRAME_SECTION);
					}
					else if (line.startsWith("frame")) {
						frame.set(Integer.parseInt(parts[1]));
						frameData.clear();
						section.set(FRAME_SECTION);
					}
				}
				else if (section.get() == HIERARCHY_SECTION) {
					if (line.startsWith("}")) {
						section.set(DEFAULT_SECTION);
					} else {
						MD5Joint joint = new MD5Joint();
						joint.name = unquote(parts[0]);
						joint.parent = Integer.parseInt(parts[1]);
						joint.flags = Integer.parseInt(parts[2]);
						joint.dataOffset = Integer.parseInt(parts[3]);
						joints.add(joint);
					}
				}
				else if (section.get() == BOUNDS_SECTION) {
					if (line.startsWith("}")) {
						section.set(DEFAULT_SECTION);
					} else {
						// Ignore
					}
				}
				else if (section.get() == BASEFRAME_SECTION) {
					if (line.startsWith("}")) {
						section.set(DEFAULT_SECTION);
					} else {
						MD5JointData data = new MD5JointData();
						data.position = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
						data.orientation = new Vector3f(Float.parseFloat(parts[6]), Float.parseFloat(parts[7]), Float.parseFloat(parts[8]));
						jointData.add(data);
					}
				}
				else if (section.get() == FRAME_SECTION) {
					if (line.startsWith("}")) {
						int numJoints = jointData.size();
						HashMap<String, Matrix4f> transforms = new HashMap<>();
						for (int i = 0; i < numJoints; i++) {
							MD5Joint joint = joints.get(i);
							MD5JointData baseframe = jointData.get(i); 
							Vector3f position = new Vector3f(baseframe.position);
							Vector3f orientation = new Vector3f(baseframe.orientation);
							int offset = 0;
							if ((joint.flags & 1) != 0) {
								position.x = frameData.get(joint.dataOffset + offset);
								offset++;
							}
							if ((joint.flags & 2) != 0) {
								position.y = frameData.get(joint.dataOffset + offset);
								offset++;
							}
							if ((joint.flags & 4) != 0) {
								position.z = frameData.get(joint.dataOffset + offset);
								offset++;
							}
							if ((joint.flags & 8) != 0) {
								orientation.x = frameData.get(joint.dataOffset + offset);
								offset++;
							}
							if ((joint.flags & 16) != 0) {
								orientation.y = frameData.get(joint.dataOffset + offset);
								offset++;
							}
							if ((joint.flags & 32) != 0) {
								orientation.z = frameData.get(joint.dataOffset + offset);
								offset++;
							}
							transforms.put(joint.name, new Matrix4f().translationRotate(position.x, position.y, position.z, 
									computeQuatW(new Quaternionf(orientation.x, orientation.y, orientation.z))));
						}
						keyframeData.add(new KeyFrameData(keyframeData.size() * delta.get(), transforms));
						section.set(DEFAULT_SECTION);
					} else {
						float[] data = toFloats(parts);
						for (int i = 0; i < data.length; i++) {
							frameData.add(data[i]);
						}
					}
				}
			});
		} catch (IOException e) {
			Log.error("Failed to import MD5 file. " + stream, e);
			return null;
		}
		return new Animation(keyframeData.size() * delta.get(), keyframeData.toArray(new KeyFrameData[0]));
	}

	private Quaternionf computeQuatW(Quaternionf quaternion) {
		float t = 1.0f - quaternion.x * quaternion.x - quaternion.y * quaternion.y - quaternion.z * quaternion.z;
		if (t < 0f) {
			quaternion.w = 0;
		} else {
			quaternion.w = (float) -Math.sqrt(t);
		}
		return quaternion;
	}
	
	private float[] toFloats(String[] parts) {
		float[] data = new float[parts.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = Float.parseFloat(parts[i]);
		}
		return data;
	}

	private String unquote(String text) {
		return text.substring(1, text.length() - 1);
	}

	@Override
	public String[] getExtensions() {
		return EXTENSIONS;
	}

}
