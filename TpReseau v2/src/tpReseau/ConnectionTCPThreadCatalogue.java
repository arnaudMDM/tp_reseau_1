package tpReseau;

import java.io.IOException;
import java.net.Socket;

public class ConnectionTCPThreadCatalogue extends ConnectionTCPThread {
	
	private static String enTeteHttpDebut = "HTTP/1.1 200 OK\r\nServer: B3302\r\nConnection: Keep-Alive\r\nContent-Type: text/txt\r\nContent-Length: ";
	private static String enTeteHttpFin = "\r\n\r\n";
	
	private String catalogue;
	
	public ConnectionTCPThreadCatalogue(Socket socket, String catalogue) throws IOException {
		super(socket);
		this.catalogue = catalogue;
	}

	protected String traiterRequete(String requete) {
		
		String reponse = enTeteHttpDebut + catalogue.length() + enTeteHttpFin + catalogue;
		return reponse;
	}
}
