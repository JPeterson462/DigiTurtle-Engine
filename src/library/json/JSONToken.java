package library.json;

public class JSONToken {
	
	public enum Type {
		
		LBRACE,
		
		RBRACE,
		
		LBRACKET,
		
		RBRACKET,
		
		COMMA,
		
		COLON,
		
		TRUE,
		
		FALSE,
		
		NULL,
		
		STRING,
		
		UNICODE,
		
		NUMBER//
		
	}
	
	private Type type;
	
	private Object value;
	
	public JSONToken(Type type, Object value) {
		this.type = type;
		this.value = value;
	}
	
	public Type getType() {
		return type;
	}
	
	public Object getValue() {
		return value;
	}

}
