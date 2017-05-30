package library.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JSONParser {
	
	public JSONValue parse(InputStream stream) throws IOException {
		return parse(tokenize(read(stream).toCharArray()));
	}
	
	public JSONValue parse(String text) {
		return parse(tokenize(text.toCharArray()));
	}
	
	private String read(InputStream stream) throws IOException {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		reader.close();
		return buffer.toString();
	}
	
	private JSONToken[] tokenize(char[] input) {
		ArrayList<JSONToken> tokens = new ArrayList<>();
		int position = 0;
		while (position < input.length) {
			if (input[position] == '"') {
				int end = seekEndOfString(input, position + 1);
				tokens.add(new JSONToken(JSONToken.Type.STRING, getString(input, position + 1, end - 1)));
				position = end;
			}
			else if (input[position] == 'u') {
				validate(input, position + 1, hexCheck);
				validate(input, position + 2, hexCheck);
				validate(input, position + 3, hexCheck);
				validate(input, position + 4, hexCheck);
				tokens.add(new JSONToken(JSONToken.Type.STRING, "" + input[position + 1] + input[position + 2] + input[position + 3] + input[position + 3]));
				position += 5;
			}
			else if (input[position] == 'n') {
				validate(input, position + 1, c -> c == 'u');
				validate(input, position + 2, c -> c == 'l');
				validate(input, position + 3, c -> c == 'l');
				tokens.add(new JSONToken(JSONToken.Type.NULL, null));
				position += 4;
			}
			else if (input[position] == 't') {
				validate(input, position + 1, c -> c == 'r');
				validate(input, position + 2, c -> c == 'u');
				validate(input, position + 3, c -> c == 'e');
				tokens.add(new JSONToken(JSONToken.Type.TRUE, null));
				position += 4;
			}
			else if (input[position] == 'f') {
				validate(input, position + 1, c -> c == 'a');
				validate(input, position + 2, c -> c == 'l');
				validate(input, position + 3, c -> c == 's');
				validate(input, position + 4, c -> c == 'e');
				tokens.add(new JSONToken(JSONToken.Type.FALSE, null));
				position += 5;
			}
			else if (input[position] == ':') {
				tokens.add(new JSONToken(JSONToken.Type.COLON, null));
				position++;
			}
			else if (input[position] == ',') {
				tokens.add(new JSONToken(JSONToken.Type.COMMA, null));
				position++;
			}
			else if (input[position] == '[') {
				tokens.add(new JSONToken(JSONToken.Type.LBRACKET, null));
				position++;
			}
			else if (input[position] == ']') {
				tokens.add(new JSONToken(JSONToken.Type.RBRACKET, null));
				position++;
			}
			else if (input[position] == '{') {
				tokens.add(new JSONToken(JSONToken.Type.LBRACE, null));
				position++;
			}
			else if (input[position] == '}') {
				tokens.add(new JSONToken(JSONToken.Type.RBRACE, null));
				position++;
			}
			else if (isNumber(input[position])) {
				int end = seekEndOfNumber(input, position);
				tokens.add(new JSONToken(JSONToken.Type.NUMBER, getString(input, position, end)));
				position = end;
			}
			else if (input[position] == ' ' || input[position] == '\t' || 
					input[position] == '\n' || input[position] == '\r') {
				position++;
			}
			else {
				throw new JSONParseException(position);
			}
		}
		return tokens.toArray(new JSONToken[0]);
	}

	private JSONValue parse(JSONToken[] tokens) {
		AtomicInteger position = new AtomicInteger();
		return parseValue(tokens, position);
	}
	
	private JSONValue parseValue(JSONToken[] tokens, AtomicInteger position) {
		JSONToken token = tokens[position.get()];
		switch (token.getType()) {
			case FALSE:
				position.incrementAndGet();
				return new JSONValue(JSONValue.Type.BOOLEAN, false);
			case LBRACE:
				position.incrementAndGet();
				JSONObject object = parseObject(tokens, position);
				position.incrementAndGet();
				return new JSONValue(JSONValue.Type.OBJECT, object);
			case LBRACKET:
				position.incrementAndGet();
				JSONArray array = parseArray(tokens, position);
				position.incrementAndGet();
				return new JSONValue(JSONValue.Type.ARRAY, array);
			case NULL:
				position.incrementAndGet();
				return new JSONValue(JSONValue.Type.NULL, null);
			case NUMBER:
				position.incrementAndGet();
				return new JSONValue(JSONValue.Type.NUMBER, Double.parseDouble(token.getValue().toString()));
			case STRING:
				position.incrementAndGet();
				return new JSONValue(JSONValue.Type.STRING, token.getValue());
			case TRUE:
				position.incrementAndGet();
				return new JSONValue(JSONValue.Type.BOOLEAN, true);
			case UNICODE:
				break;
			default:
				break;
		}
		return null;
	}
	
	private JSONArray parseArray(JSONToken[] tokens, AtomicInteger position) {
		ArrayList<JSONValue> values = new ArrayList<>();
		while (!tokens[position.get()].getType().equals(JSONToken.Type.RBRACKET)) {
			values.add(parseValue(tokens, position));
			if (tokens[position.get()].getType().equals(JSONToken.Type.COMMA)) {
				position.incrementAndGet();
			} else if (tokens[position.get()].getType().equals(JSONToken.Type.RBRACKET)) {
				// The while loop will exit
			} else {
				throw new JSONParseException(position.get());
			}
		}
		return new JSONArray(values.toArray(new JSONValue[0]));
	}
	
	private JSONObject parseObject(JSONToken[] tokens, AtomicInteger position) {
		HashMap<String, JSONValue> attributes = new HashMap<>();
		while (!tokens[position.get()].getType().equals(JSONToken.Type.RBRACE)) {
			String string = tokens[position.get()].getValue().toString();
			position.incrementAndGet();
			if (tokens[position.get()].getType().equals(JSONToken.Type.COLON)) {
				// Valid
			} else {
				throw new JSONParseException(position.get());
			}
			position.incrementAndGet();
			JSONValue value = parseValue(tokens, position);
			attributes.put(string, value);
			if (tokens[position.get()].getType().equals(JSONToken.Type.COMMA)) {
				position.incrementAndGet();
			} else if (tokens[position.get()].getType().equals(JSONToken.Type.RBRACE)) {
				// The while loop will exit
			} else {
				throw new JSONParseException(position.get());
			}
		}
		return new JSONObject(attributes);
	}

	// Helper Methods
	
	@FunctionalInterface
	interface CharacterFilter {
		boolean valid(char c);
	}
	
	private void validate(char[] input, int position, CharacterFilter filter) {
		if (!filter.valid(input[position])) {
			throw new JSONParseException(position);
		}
	}
	
	private CharacterFilter hexCheck = (c) -> isHex(c);
	private boolean isHex(char c) {
		return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}
	
	// start <= char < end
	private String getString(char[] input, int start, int end) {
		char[] subArray = new char[end - start];
		System.arraycopy(input, start, subArray, 0, subArray.length);
		return new String(subArray);
	}
	
	private int seekEndOfString(char[] input, int start) {
		for (int i = start; i < input.length; i++) {
			if (input[i] == '"' && input[i - 1] != '\\') {
				return i + 1;
			}
		}
		return input.length;
	}
	
	private int seekEndOfNumber(char[] input, int start) {
		for (int i = start; i < input.length; i++) {
			char c = input[i];
			if (isNumber(c)) {
				// Valid
			} else {
				return i;
			}
		}
		return input.length;
	}
	
	private boolean isNumber(char c) {
		return c == '-' || c == '+' || c == 'E' || c == 'e' || isInteger(c);
	}
	
	private boolean isInteger(char c) {
		return c >= '0' && c <= '9';
	}
	
}
