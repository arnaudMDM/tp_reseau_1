package tpReseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionEnvoiTCP {
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	public ConnectionEnvoiTCP(Socket socket) throws IOException {
		this.socket = socket;
		out = new PrintWriter(socket.getOutputStream(), false);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public void envoyer(String requete) {
		out.print(requete);
		out.flush();
	}
	
	public String recevoir() {
		StringBuilder requete = new StringBuilder();
		int etat;
		int c = -1;
		for (;;) {
    		etat = 0;
			try {
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
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			if (c == -1) {
				System.err.println("Client déconnecté depuis " + socket.getInetAddress());
				return null;
			}
			
			System.out.println("Requete de " + socket.getInetAddress() + " : " + requete);
			
			return requete.toString();
    	}
	}
}
