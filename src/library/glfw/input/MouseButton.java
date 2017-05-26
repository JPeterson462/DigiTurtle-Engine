package library.glfw.input;

import org.lwjgl.glfw.GLFW;

public class MouseButton {

	public static MouseButton LEFT = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_LEFT), MIDDLE = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_MIDDLE),
			RIGHT = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_RIGHT), BUTTON_1 = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_1), 
			BUTTON_2 = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_2), BUTTON_3 = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_3), 
			BUTTON_4 = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_4), BUTTON_5 = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_5), 
			BUTTON_6 = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_6), BUTTON_7 = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_7), 
			BUTTON_8 = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_8);
	
	private int id;

	public MouseButton(int id) {
		this.id = id;
	}

	public boolean equals(Object o) {
		return o instanceof MouseButton && id == ((MouseButton) o).id;
	}

	public int hashCode() {
		return Integer.hashCode(id);
	}

}
