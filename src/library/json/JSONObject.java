package library.json;

import java.util.HashMap;
import java.util.Set;

public class JSONObject {
	
	private HashMap<String, JSONValue> attributes;
	
	public JSONObject(HashMap<String, JSONValue> attributes) {
		this.attributes = attributes;
	}
	
	public Set<String> getKeys() {
		return attributes.keySet();
	}
	
	public JSONValue getAttribute(String name) {
		return attributes.get(name);
	}
	
	public HashMap<String, JSONValue> getAttributes() {
		return attributes;
	}

}
