package tpReseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/** Classe servant � mod�liser les interractions directes avec chaque client.<br />
 * Une instance de cette classe est cr��e pour chaque client et ces instances fonctionnent en parall�le.<br />
 * <br />
 * Il s'agit d'une classe interne afin que les membres de la classe globale puissent lui �tre accessibles.
 * 
 * @author INFO2 2010-2011, groupe de PT n�9<br />
 * Contributeur principal : Robin Gicquel
 */
abstract class ConnectionTCPThread extends Thread {
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	private boolean marche;
	
	/** Cr�e un objet ServeurThread.
	 * 
	 * @param socket objet socket relatif au client
	 * @throws IOException en cas d'erreur d'entr�e-sortie, afin qu'elle soit g�r�es par la classe appelante
	 */
	public ConnectionTCPThread(Socket socket) throws IOException {
		this.socket = socket;
		out = new PrintWriter(socket.getOutputStream(), false);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		marche = true;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void run() {
		System.out.println("Nouvelle connexion depuis " + socket.getInetAddress() + " sur le port " + socket.getLocalPort());
		
		StringBuilder requete = new StringBuilder();
		String reponse;
		int c;
		int etat;
	    try {
	    	while (marche) {
	    		etat = 0;
				while ((c = in.read()) != -1) {
					//System.out.print(c+"~"+(char)c);
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
					System.err.println("Client d�connect� depuis " + socket.getInetAddress());
					return;
				}
				
				System.out.println("Requete de " + socket.getInetAddress() + " : " + requete);
				
				reponse = traiterRequete(requete.toString());
				if (reponse != null && marche) {
					out.print(reponse);
					out.flush();
					
					System.out.println("Envoy� !!");
				}
	    	}
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected abstract String traiterRequete(String requete);
	
	/** Ferme proprement les flux li�s � cet objet avant sa destruction.
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
