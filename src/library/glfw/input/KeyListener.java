package library.glfw.input;

@FunctionalInterface
public interface KeyListener {

	public void onEvent(KeyEvent event, Object data, Modifiers modifiers);
	
}
