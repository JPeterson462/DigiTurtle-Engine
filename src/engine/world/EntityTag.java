package engine.world;

public class EntityTag {
	
	private int id;
	
	public EntityTag(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	public boolean equals(Object object) {
		return object instanceof EntityTag && ((EntityTag) object).id == id;
	}

}
