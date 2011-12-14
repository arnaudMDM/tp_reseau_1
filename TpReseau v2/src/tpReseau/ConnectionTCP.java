package tpReseau;

import ihm.Ihm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class ConnectionTCP extends Connection {

	private ServerSocket serverSocket;

	public ConnectionTCP(int portEcoute, Ihm ihm) {

		super(portEcoute, ihm);

		serverSocket = null;
	}

	@Override
	protected void ouvrirSocket() throws IOException {
		serverSocket = new ServerSocket(portEcoute);
	}

	@Override
	protected void ecoute() throws IOException {
		Socket socket = serverSocket.accept(); // mise en écoute
		Thread thr = creerThreadClient(socket); // création d'un objet
												// ServeurThread pour répondre
												// aux requêtes de chaque client
		thr.start(); // démarrage de ServeurThread dans un nouveau thread
						// parallèle
	}

	@Override
	void terminer() throws IOException {
		serverSocket.close();
	}
	
	@Override
	void timeoutDepasse() {
		// vide
	}

	abstract Thread creerThreadClient(Socket socket)
			throws IOException;

}
