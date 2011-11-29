package tpReseau;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionTCPThreadVideo extends ConnectionTCPThread {

	private static final int ETAT_1 = 0, ETAT_2 = 1;
	
	private String id;
	private int etat;
	private Socket socketDonnees;
	private OutputStream outDonnees;

	public ConnectionTCPThreadVideo(Socket socket, String id)
			throws IOException {
		super(socket);
		this.id = id;
		etat = ETAT_1;
	}

	@Override
	protected String traiterRequete(String requete) {
		Scanner sc = new Scanner(requete);
		String str;
		
		switch (etat) {
		case ETAT_1 :
			if (!(str = sc.nextLine()).startsWith("GET "))
				return null;
			String idRecu = str.substring(4);
	
			System.out.println("ID reçu : " + idRecu);
	
			if (!(str = sc.nextLine()).startsWith("LISTEN_PORT "))
				return null;
			int clientPort = -1;
			try {
				clientPort = Integer.parseInt(str.substring(12));
			} catch (NumberFormatException e) {
				return null;
			}
	
			System.out.println("clientPort reçu : " + clientPort);
	
			if (!sc.nextLine().equals(""))
				return null;
	
			if (!idRecu.equals(id))
				return null;
	
			// ok, on ouvre la connection de données
	
			try {
				socketDonnees = new Socket(getSocket().getInetAddress(), clientPort);
				outDonnees = socketDonnees.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			etat = ETAT_2;
			break;
		case ETAT_2 :
			
			str = sc.nextLine();
			if (str.equals("END")){
				terminer();
				break;
			}
			
			if (!str.startsWith("GET "))
				return null;
			
			String imageId = str.substring(4);
			
			envoyerImage(imageId);
			
			break;
			
		}

		return null;
	}
	
	private void envoyerImage(String imageId) {
		String str1 = "5";
		
	}
	
	protected void terminer() {
		super.terminer();
		try {
			outDonnees.close();
			socketDonnees.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
