package engine.networking;

import java.util.HashMap;

public class Registry {
	
	private HashMap<Integer, Class<?>> idToType = new HashMap<>();
	
	private HashMap<Class<?>, Integer> typeToId = new HashMap<>();
	
	private int size = 0;
	
	public Registry link(Class<?> type) {
		size++;
		return link(size, type);
	}
	
	public Registry link(int id, Class<?> type) {
		if (id < 0) {
			throw new IllegalArgumentException("id must be >= 0!");
		}
		idToType.put(id, type);
		typeToId.put(type, id);
		return this;
	}
	
	public Class<?> lookup(int id) {
		return idToType.get(id);
	}
	
	public int lookup(Class<?> type) {
		int id = typeToId.getOrDefault(type, -1);
		while (id < 0) {
			type = type.getSuperclass();
			id = typeToId.getOrDefault(type, -1);
		}
		return id;
	}

}
