package library.glfw.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;

import utils.Pair;

public class Input {
	
	private HashMap<MouseEvent, ArrayList<MouseListener>> mouseListeners = new HashMap<>();
	
	private HashMap<KeyEvent, ArrayList<KeyListener>> keyListeners = new HashMap<>();
	
	private FilesListener filesListener = (files) -> {};
	
	private Modifiers modifiers = new Modifiers(0);
	
	private Vector2d cursorPosition = new Vector2d();
	
	private HashMap<MouseButton, Boolean> buttonStates = new HashMap<>();
	
	private int windowPosCounter;
	
	public Input(long window) {
		GLFW.glfwSetCharModsCallback(window, (w, letter, mods) -> {
			if (w == window) {
				modifiers = new Modifiers(mods);
				ArrayList<KeyListener> listeners = keyListeners.get(KeyEvent.LETTER_TYPED);
				if (listeners != null) {
					for (int i = 0; i < listeners.size(); i++) {
						listeners.get(i).onEvent(KeyEvent.LETTER_TYPED, (char) letter, modifiers);
					}
				}
			}
		});
		GLFW.glfwSetCursorEnterCallback(window, (w, entered) -> 
			windowPosCounter = entered ? 0 : -Integer.MAX_VALUE);
		GLFW.glfwSetCursorPosCallback(window, (w, x, y) -> {
			if (w == window) {
				double dx = x - cursorPosition.x, dy = y - cursorPosition.y;
				cursorPosition.set(x, y);
				if (windowPosCounter > 0) {
					ArrayList<MouseListener> listeners = mouseListeners.get(MouseEvent.CURSOR_TRAVEL);
					if (listeners != null) {
						for (int i = 0; i < listeners.size(); i++) {
							listeners.get(i).onEvent(MouseEvent.CURSOR_TRAVEL, new Vector2d(x, y), modifiers);
						}
					}
					listeners = mouseListeners.get(MouseEvent.DRAG);
					if (listeners != null) {
						for (int i = 0; i < listeners.size(); i++) {
							for (Map.Entry<MouseButton, Boolean> buttonState : buttonStates.entrySet()) {
								if (buttonState.getValue()) {
									listeners.get(i).onEvent(MouseEvent.DRAG, new Pair<Vector2d, MouseButton>(new Vector2d(dx, dy), buttonState.getKey()), modifiers);
								}
							}
						}
					}
				}
				windowPosCounter++;
			}
		});
		GLFW.glfwSetDropCallback(window, (w, count, namePointers) -> {
			if (w == window) {
				String[] names = new String[count];
				for (int i = 0; i < names.length; i++) {
					names[i] = GLFWDropCallback.getName(namePointers, i);
				}
				filesListener.onDropFiles(names);
			}
		});
		GLFW.glfwSetKeyCallback(window, (w, keyId, scancode, action, mods) -> {
			if (w == window) {
				Key key = new Key(keyId);
				modifiers = new Modifiers(mods);
				ArrayList<KeyListener> listeners = keyListeners.get(KeyEvent.KEY_TYPED);
				if (listeners != null && action != GLFW.GLFW_PRESS) {
					for (int i = 0; i < listeners.size(); i++) {
						listeners.get(i).onEvent(KeyEvent.KEY_TYPED, key, modifiers);
					}
				}
				listeners = keyListeners.get(KeyEvent.KEY_DOWN);
				if (listeners != null && action == GLFW.GLFW_PRESS) {
					for (int i = 0; i < listeners.size(); i++) {
						listeners.get(i).onEvent(KeyEvent.KEY_DOWN, key, modifiers);
					}
				}
				listeners = keyListeners.get(KeyEvent.KEY_UP);
				if (listeners != null && action == GLFW.GLFW_RELEASE) {
					for (int i = 0; i < listeners.size(); i++) {
						listeners.get(i).onEvent(KeyEvent.KEY_UP, key, modifiers);
					}
				}
			}
		});
		GLFW.glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
			if (w == window) {
				MouseButton mouseButton = new MouseButton(button);
				modifiers = new Modifiers(mods);
				ArrayList<MouseListener> listeners = mouseListeners.get(MouseEvent.CLICK);
				if (listeners != null && action == GLFW.GLFW_RELEASE) {
					for (int i = 0; i < listeners.size(); i++) {
						listeners.get(i).onEvent(MouseEvent.CLICK, new Pair<Vector2d, MouseButton>(new Vector2d(cursorPosition), mouseButton), modifiers);
					}
				}
				listeners = mouseListeners.get(MouseEvent.MOUSE_DOWN);
				if (listeners != null && action == GLFW.GLFW_PRESS) {
					for (int i = 0; i < listeners.size(); i++) {
						listeners.get(i).onEvent(MouseEvent.MOUSE_DOWN, mouseButton, modifiers);
					}
				}
				listeners = mouseListeners.get(MouseEvent.MOUSE_UP);
				if (listeners != null && action == GLFW.GLFW_RELEASE) {
					for (int i = 0; i < listeners.size(); i++) {
						listeners.get(i).onEvent(MouseEvent.MOUSE_UP, mouseButton, modifiers);
					}
				}
				buttonStates.put(mouseButton, action != GLFW.GLFW_RELEASE);
			}
		});
		GLFW.glfwSetScrollCallback(window, (w, dx, dy) -> {
			if (w == window) {
				ArrayList<MouseListener> listeners = mouseListeners.get(MouseEvent.SCROLL);
				if (listeners != null) {
					for (int i = 0; i < listeners.size(); i++) {
						listeners.get(i).onEvent(MouseEvent.SCROLL, new Vector2d(dx, dy), modifiers);
					}
				}
			}
		});
	}
	
	private <T, K> void addListenerInternal(T listener, K event, HashMap<K, ArrayList<T>> map) {
		ArrayList<T> listenersSoFar = map.get(event);
		if (listenersSoFar != null) {
			listenersSoFar.add(listener);
		} else {
			listenersSoFar = new ArrayList<>();
			listenersSoFar.add(listener);
			map.put(event, listenersSoFar);
		}
	}
	
	public void addListener(MouseListener listener, MouseEvent event0, MouseEvent... otherEvents) {
		addListenerInternal(listener, event0, mouseListeners);
		for (int i = 0; i < otherEvents.length; i++) {
			addListenerInternal(listener, otherEvents[i], mouseListeners);
		}
	}
	
	public void addListener(KeyListener listener, KeyEvent event0, KeyEvent... otherEvents) {
		addListenerInternal(listener, event0, keyListeners);
		for (int i = 0; i < otherEvents.length; i++) {
			addListenerInternal(listener, otherEvents[i], keyListeners);
		}
	}
	
	public void setFilesListener(FilesListener listener) {
		filesListener = listener;
	}
	
	public Vector2d getCursorPosition() {
		return cursorPosition;
	}

}
