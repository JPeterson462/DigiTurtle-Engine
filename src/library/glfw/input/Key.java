package library.glfw.input;

import java.lang.reflect.Field;

import org.lwjgl.glfw.GLFW;

public class Key {

	public static Key SPACE = new Key(0), APOSTROPHE = new Key(0), COMMA = new Key(0),
			MINUS = new Key(0), PERIOD = new Key(0), SLASH = new Key(0), NUM_0 = new Key(0),
			NUM_1 = new Key(0), NUM_2 = new Key(0), NUM_3 = new Key(0), NUM_4 = new Key(0),
			NUM_5 = new Key(0), NUM_6 = new Key(0), NUM_7 = new Key(0), NUM_8 = new Key(0),
			NUM_9 = new Key(0), SEMICOLON = new Key(0), EQUAL = new Key(0), A = new Key(0),
			B = new Key(0), C = new Key(0), D = new Key(0), E = new Key(0), F = new Key(0),
			G = new Key(0), H = new Key(0), I = new Key(0), J = new Key(0), K = new Key(0),
			L = new Key(0), M = new Key(0), N = new Key(0), O = new Key(0), P = new Key(0),
			Q = new Key(0), R = new Key(0), S = new Key(0), T = new Key(0), U = new Key(0),
			V = new Key(0), W = new Key(0), X = new Key(0), Y = new Key(0), Z = new Key(0),
			LEFT_BRACKET = new Key(0), BACKSLASH = new Key(0), RIGHT_BRACKET = new Key(0),
			GRAVE_ACCENT = new Key(0), WORLD_1 = new Key(0), WORLD_2 = new Key(0),
			ESCAPE = new Key(0), ENTER = new Key(0), TAB = new Key(0), BACKSPACE = new Key(0),
			INSERT = new Key(0), DELETE = new Key(0), RIGHT = new Key(0), LEFT = new Key(0),
			DOWN = new Key(0), UP = new Key(0), PAGE_UP = new Key(0), PAGE_DOWN = new Key(0),
			HOME = new Key(0), END = new Key(0), CAPS_LOCK = new Key(0), SCROLL_LOCK = new Key(0),
			NUM_LOCK = new Key(0), PRINT_SCREEN = new Key(0), PAUSE = new Key(0), F1 = new Key(0),
			F2 = new Key(0), F3 = new Key(0), F4 = new Key(0), F5 = new Key(0), F6 = new Key(0),
			F7 = new Key(0), F8 = new Key(0), F9 = new Key(0), F10 = new Key(0), F11 = new Key(0),
			F12 = new Key(0), F13 = new Key(0), F14 = new Key(0), F15 = new Key(0), F16 = new Key(0),
			F17 = new Key(0), F18 = new Key(0), F19 = new Key(0), F20 = new Key(0), F21 = new Key(0),
			F22 = new Key(0), F23 = new Key(0), F24 = new Key(0), F25 = new Key(0), KP_0 = new Key(0),
			KP_1 = new Key(0), KP_2 = new Key(0), KP_3 = new Key(0), KP_4 = new Key(0),
			KP_5 = new Key(0), KP_6 = new Key(0), KP_7 = new Key(0), KP_8 = new Key(0),
			KP_9 = new Key(0), KP_DECIMAL = new Key(0), KP_DIVIDE = new Key(0),
			KP_MULTIPLY = new Key(0), KP_SUBTRACT = new Key(0), KP_ADD = new Key(0),
			KP_ENTER = new Key(0), KP_EQUAL = new Key(0), LEFT_SHIFT = new Key(0),
			LEFT_CONTROL = new Key(0), LEFT_ALT = new Key(0), LEFT_SUPER = new Key(0),
			RIGHT_SHIFT = new Key(0), RIGHT_CONTROL = new Key(0), RIGHT_ALT = new Key(0),
			RIGHT_SUPER = new Key(0), MENU = new Key(0);
	
	static {
		Field[] fields = Key.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String glfwName = "GLFW_KEY_" + field.getName();
			if (field.getName().startsWith("NUM_") && field.getName().length() == 5) {
				glfwName = "GLFW_KEY_" + field.getName().substring(4);
			}
			try {
				((Key) field.get(null)).id = GLFW.class.getDeclaredField(glfwName).getInt(null);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	private int id;

	public Key(int id) {
		this.id = id;
	}

	public boolean equals(Object o) {
		return o instanceof Key && id == ((Key) o).id;
	}

	public int hashCode() {
		return Integer.hashCode(id);
	}

}
