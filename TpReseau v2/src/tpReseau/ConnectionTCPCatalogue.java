package tpReseau;

import java.io.IOException;
import java.net.Socket;

public class ConnectionTCPCatalogue extends ConnectionTCP {
	
	private String catalogue;
	
	public ConnectionTCPCatalogue(int portEcoute, String catalogue) {
		super(portEcoute);
		this.catalogue = catalogue;
	}
	
	Thread creerThreadClient(Socket socket) throws IOException {
		return new ConnectionTCPThreadCatalogue(socket, catalogue);
	}
}
