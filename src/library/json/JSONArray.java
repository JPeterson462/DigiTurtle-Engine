package library.json;

import java.util.Iterator;

public class JSONArray implements Iterable<JSONValue> {
	
	private JSONValue[] values;
	
	public JSONArray(JSONValue[] values) {
		this.values = values;
	}
	
	public JSONValue getValue(int index) {
		return values[index];
	}
	
	public int length() {
		return values.length;
	}
	
	public JSONValue[] getValues() {
		return values;
	}

	@Override
	public Iterator<JSONValue> iterator() {
		return new Iterator<JSONValue>() {

			private int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < values.length;
			}

			@Override
			public JSONValue next() {
				return values[index++];
			}
			
		};
	}

}
