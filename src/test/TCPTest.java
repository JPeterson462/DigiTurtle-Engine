package test;

import java.io.Serializable;
import engine.AssetInputStream;
import engine.networking.Client;
import engine.networking.NetworkSettings;
import engine.networking.Protocol;
import engine.networking.Server;
import engine.networking.coders.SerializedCoder;
import engine.networking.coders.StringCoder;
import library.json.JSONParser;
import library.json.JSONValue;

public class TCPTest {
	
	public static void main(String[] args) throws Exception {
		NetworkSettings.UDP_PACKET_SIZE = 1024;
		Server s = new Server(Protocol.UDP, "127.0.0.1", 9840).listen(String.class, (o, end, write) -> {
			System.out.println("Message: " + o);
			write.write("Hey There");
		}).listen(TCPObject.class, (o, end, write) -> {
			System.out.println("TCP Message: " + o);
			write.write("What's Up?");
		});
		s.register(new StringCoder());
		s.register(new SerializedCoder());
		s.getRegistry().link(String.class);
		s.getRegistry().link(TCPObject.class);
		s.start();
		Client c = new Client(Protocol.UDP, "127.0.0.1", 9840).listen(Object.class, (o, end, write) -> {
			System.out.println("Message: " + o);
		});
		c.register(new StringCoder());
		c.register(new SerializedCoder());
		c.getRegistry().link(String.class);
		c.getRegistry().link(TCPObject.class);
		c.start();
		for (int i = 0; i < 1_000_000; i++) {}
		c.write("Hello");
		c.write(new TCPObject("World"));
		
//		RESTInterface rest = new RESTInterface("https://www.google.com/search");
//		HashMap<String, String> properties = new HashMap<String, String>();
//		properties.put("q", "google get request");
//		properties.put("ie", "utf-8");
//		properties.put("oe", "utf-8");
//		System.out.println(rest.execute(HTTPRequest.GET, properties));
		
		//http://httpbin.org/get
//		RESTInterface rest = new RESTInterface("http://httpbin.org/get");
//		String response = (rest.execute(HTTPRequest.GET, null));
		JSONParser parser = new JSONParser();
//		JSONValue value = parser.parse(response);
//		System.out.println(response);
//		System.out.println(value.asJSONObject().getAttribute("origin").asString());
		
		long st = System.nanoTime();
		JSONValue value = parser.parse(new AssetInputStream("sample.json"));
		long dt = System.nanoTime() - st;
		// 20k lines
		long nsPerLine = dt / 20_000;
		System.out.println((dt / 1_000_000) + "ms");
		System.out.println(nsPerLine + "ns per line");
	}
	
	static class TCPObject implements Serializable {

		private static final long serialVersionUID = -911047227009497842L;

		private String data;
		
		public TCPObject() {
			
		}
		
		public TCPObject(String data) {
			setData(data);
		}
		
		public void setData(String data) {
			this.data = data;
		}
		
		public String toString() {
			return data;
		}
		
	}

}
