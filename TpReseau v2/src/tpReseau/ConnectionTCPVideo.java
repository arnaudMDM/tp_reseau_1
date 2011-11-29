package tpReseau;

import java.io.IOException;
import java.net.Socket;

public class ConnectionTCPVideo extends ConnectionTCP {
	
	private String id;
	
	public ConnectionTCPVideo(int portEcoute, String id) {
		super(portEcoute);
		this.id = id;
	}
	
	Thread creerThreadClient(Socket socket) throws IOException {
		return new ConnectionTCPThreadVideo(socket, id);
	}
}