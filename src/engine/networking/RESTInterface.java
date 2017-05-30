package engine.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RESTInterface {

	private String url;

	private final String ENCODING_TYPE = "UTF-8";
	
	public RESTInterface(String url) {
		this.url = url;
	}

	private String formGetURL(HashMap<String, String> properties) throws UnsupportedEncodingException {
		if (properties == null || properties.size() == 0) {
			return url;
		}
		return url + "?" + formString(properties);
	}
	
	private String formString(HashMap<String, String> properties) throws UnsupportedEncodingException {
		if (properties == null) {
			return "";
		}
		String formatted = "";
		for (Map.Entry<String, String> property : properties.entrySet()) {
			if (formatted.length() > 0) {
				formatted += "&";
			}
			formatted += property.getKey() + "=" + URLEncoder.encode(property.getValue(), ENCODING_TYPE);
		}
		return formatted;
	}

	public String execute(HTTPRequest request, HashMap<String, String> properties) throws MalformedURLException, IOException {
		final String POST_TYPE = "application/x-www-form-urlencoded";
		HttpURLConnection conn;
		switch (request) {
			case GET:
				StringBuilder result = new StringBuilder();
				String getUrl = formGetURL(properties);
				conn = (HttpURLConnection) new URL(getUrl).openConnection();
				conn.setRequestMethod("GET");
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				rd.close();
				return result.toString();
			case POST:
				String rawData = formString(properties);
				String encodedData = URLEncoder.encode(rawData, ENCODING_TYPE);
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", POST_TYPE);
				conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
				conn.getOutputStream().write(encodedData.getBytes());
				return "";
		}
		return null;
	}

}
