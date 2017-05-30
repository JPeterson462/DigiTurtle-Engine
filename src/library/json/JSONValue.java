package library.json;

public class JSONValue {
	
	public enum Type {
		
		// Attributes
		
		STRING,
		
		UNICODE,
		
		NUMBER,
		
		BOOLEAN,
		
		NULL,
		
		// Nested Elements
		
		OBJECT,
		
		ARRAY
		
	}
	
	private Type type;
	
	private Object value;
	
	public JSONValue(Type type, Object value) {
		this.type = type;
		this.value = value;
	}
	
	public Type getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}
	
	public String asString() {
		return (String) value;
	}
	
	public String asUnicode() {
		return (String) value;
	}
	
	public Double asNumber() {
		return (Double) value;
	}
	
	public Boolean asBoolean() {
		return (Boolean) value;
	}
	
	public Object asNull() {
		return null;
	}
	
	public JSONObject asJSONObject() {
		return (JSONObject) value;
	}
	
	public JSONArray asJSONArray() {
		return (JSONArray) value;
	}

}
