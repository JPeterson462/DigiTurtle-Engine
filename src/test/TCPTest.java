package test;

import engine.networking.Client;
import engine.networking.Protocol;
import engine.networking.Server;
import engine.networking.coders.StringCoder;

public class TCPTest {
	
	public static void main(String[] args) {
		Server s = new Server(Protocol.TCP, "127.0.0.1", 9840).listen((o, end, write) -> {
			System.out.println("Message: " + o);
			write.write("Hey There");
		});
		s.register(new StringCoder());
		s.getRegistry().link(String.class);
		s.start();
		Client c = new Client(Protocol.TCP, "127.0.0.1", 9840).listen((o, end, write) -> {
			System.out.println("Message: " + o);
		});
		c.register(new StringCoder());
		c.getRegistry().link(String.class);
		c.start();
		for (int i = 0; i < 1_000_000; i++) {}
		c.write("Hello");
		c.write("World");
	}

}
