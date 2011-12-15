package tpReseau.tcp;

import ihm.Ihm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/** Classe servant à modéliser les interractions directes avec chaque client.<br />
 * Une instance de cette classe est créée pour chaque client et ces instances fonctionnent en parallèle.<br />
 * <br />
 * Il s'agit d'une classe interne afin que les membres de la classe globale puissent lui être accessibles.
 */
abstract class ConnectionTCPThread extends Thread {
	
	private Socket socket;
	private BufferedWriter out;
	private BufferedReader in;
	
	protected Ihm ihm;
	
	private boolean marche;
	
	/** Crée un objet ServeurThread.
	 * 
	 * @param socket objet socket relatif au client
	 * @throws IOException en cas d'erreur d'entrée-sortie, afin qu'elle soit gérées par la classe appelante
	 */
	public ConnectionTCPThread(Socket socket, Ihm ihm) throws IOException {
		this.socket = socket;
		out = new BufferedWriter(new PrintWriter(socket.getOutputStream(), false));
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		this.ihm = ihm;
		
		marche = true;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void run() {
		ihm.afficher("Nouvelle connection depuis " + socket.getInetAddress() + " sur le port " + socket.getLocalPort());
		
		StringBuilder requete;
		String reponse;
		int c;
		int etat;
	    try {
	    	while (marche) {
	    		etat = 0;
	    		requete = new StringBuilder();
				while ((c = in.read()) != -1) {
					requete.append((char)c);
					
					switch (etat) {
					case 0:
						if (c == '\r') {
							etat++;
						}
						break;
					case 1 :
						if (c == '\n') {
							etat++;
						}
						else {
							etat = 0;
						}
						break;
					case 2 :
						if (c == '\r') {
							etat++;
						}
						else {
							etat = 0;
						}
						break;
					case 3 :
						if (c == '\n') {
							etat++;
						}
						else {
							etat = 0;
						}
						break;
					}
					
					if (etat > 3)
						break;	
				}
				if (c == -1) {
					throw new IOException();
				}
				
				reponse = traiterRequete(requete.toString());
				if (reponse != null && marche) {
					out.write(reponse);
					out.flush();
				}
	    	}
	    	
		} catch (IOException e) {
			//e.printStackTrace();
			ihm.clientDeconnecte(socket.getInetAddress().toString());
			terminer();
		}
	    
	    ihm.afficher("Fin de la connection depuis " + socket.getInetAddress() + " sur le port " + socket.getLocalPort());
	}
	
	protected abstract String traiterRequete(String requete);
	
	/** Ferme proprement les flux liés à cet objet avant sa destruction.
	 */
	protected void terminer() {
		marche = false;
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
