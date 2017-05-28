package engine.rendering;

public interface InstancedGeometry<T extends InstanceTemplate> {
	
	public void updateInstance(int instance, T newValue);
	
	public void bind();
	
	public void render(int instanceCount);
	
	public void unbind();
	
	public void delete();

}
