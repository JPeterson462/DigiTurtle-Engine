package library.glfw.input;

@FunctionalInterface
public interface FilesListener {

	public void onDropFiles(String[] paths);
	
}
