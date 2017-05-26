package library.glfw.input;

public enum MouseEvent {
	
	/**
	 * object = Vector2d
	 * <br>
	 * Scroll Delta
	 */
	SCROLL,
	
	/**
	 * object = Vector2d
	 * <br>
	 * Cursor Position
	 */
	CURSOR_TRAVEL,
	
	/**
	 * object = Pair<Vector2d, MouseButton>
	 * <br>
	 * Cursor Position, Mouse Button
	 */
	CLICK,
	
	/**
	 * object = MouseButton
	 * <br>
	 * Mouse Button
	 */
	MOUSE_UP,
	
	/**
	 * object = MouseButton
	 * <br>
	 * Mouse Button
	 */
	MOUSE_DOWN,
	
	/**
	 * object = Pair<Vector2d, MouseButton>
	 * <br>
	 * Cursor Delta, Mouse Button
	 */
	DRAG;

}
