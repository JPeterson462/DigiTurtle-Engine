package library.glfw.input;

@FunctionalInterface
public interface MouseListener {
	
	public void onEvent(MouseEvent event, Object data, Modifiers modifiers);

}
