package engine.skeleton;

import engine.world.Component;
import engine.world.Entity;
import library.models.Model;

public class SkeletonComponent implements Component {
	
	private Skeleton skeleton;
	
	public SkeletonComponent(Model model) {
		skeleton = new Skeleton(model);
	}
	
	public Skeleton getSkeleton() {
		return skeleton;
	}
	
	@Override
	public void update(Entity entity, float delta) {
		
	}

}
