package library.json;

public class JSONParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public JSONParseException(int position) {
		super("Invalid character at position " + position);
	}

}
