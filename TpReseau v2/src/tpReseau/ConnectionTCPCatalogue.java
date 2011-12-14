package tpReseau;

import ihm.Ihm;

import java.io.IOException;
import java.net.Socket;

public class ConnectionTCPCatalogue extends ConnectionTCP {
	
	private String catalogue;
	
	public ConnectionTCPCatalogue(int portEcoute, String catalogue, Ihm ihm) {
		super(portEcoute, ihm);
		this.catalogue = catalogue;
	}
	
	Thread creerThreadClient(Socket socket) throws IOException {
		return new ConnectionTCPThreadCatalogue(socket, catalogue, ihm);
	}
}
