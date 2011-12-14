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
		Socket socket = serverSocket.accept(); // mise en �coute
		Thread thr = creerThreadClient(socket); // cr�ation d'un objet
												// ServeurThread pour r�pondre
												// aux requ�tes de chaque client
		thr.start(); // d�marrage de ServeurThread dans un nouveau thread
						// parall�le
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
