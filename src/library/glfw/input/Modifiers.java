package library.glfw.input;

import org.lwjgl.glfw.GLFW;

public class Modifiers {
	
	private boolean altDown, shiftDown, controlDown, superDown;

	public Modifiers(int glfw) {
		altDown = bitSet(glfw, GLFW.GLFW_MOD_ALT);
		shiftDown = bitSet(glfw, GLFW.GLFW_MOD_SHIFT);
		controlDown = bitSet(glfw, GLFW.GLFW_MOD_CONTROL);
		superDown = bitSet(glfw, GLFW.GLFW_MOD_SUPER);
	}
	
	private boolean bitSet(int value, int bit) {
		return (value & bit) != 0;
	}

	public boolean isAltDown() {
		return altDown;
	}

	public boolean isShiftDown() {
		return shiftDown;
	}

	public boolean isControlDown() {
		return controlDown;
	}

	public boolean isSuperDown() {
		return superDown;
	}
	
}
