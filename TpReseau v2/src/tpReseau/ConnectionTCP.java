package tpReseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public abstract class ConnectionTCP extends Thread {
	
	private int portEcoute;
	
	private ServerSocket serverSocket;
	
	private boolean marche;
	
	public ConnectionTCP(int portEcoute) {
		
		this.portEcoute = portEcoute;
		
		serverSocket = null;
		
		marche = true;
	}
	
	@Override
	public void run() {
		try {
            serverSocket = new ServerSocket(portEcoute);
        } catch (IOException e) {
            System.err.println("Impossible d'ouvrir le port " + portEcoute);
            System.exit(1);
        }
        System.out.println("Serveur en �coute");
        
        while (marche) {
			try {
				Socket socket = serverSocket.accept(); // mise en �coute
				Thread thr = creerThreadClient(socket); // cr�ation d'un objet ServeurThread pour r�pondre aux requ�tes de chaque client
				thr.start(); // d�marrage de ServeurThread dans un nouveau thread parall�le
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	abstract Thread creerThreadClient(Socket socket) throws IOException;
	
}
