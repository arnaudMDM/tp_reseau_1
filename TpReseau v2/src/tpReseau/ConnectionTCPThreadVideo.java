package tpReseau;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ConnectionTCPThreadVideo extends ConnectionTCPThread implements EnvoiImage {

	private static final int ETAT_1 = 0, ETAT_2_PULL = 1, ETAT_2_PUSH = 2, ETAT_3_PUSH = 3;

	private int type;
	
	private String id;
	private int etat;
	private int indexImage;

	private Socket socketDonnees;
	private OutputStream outDonnees;
	
	private ThreadEnvoiPush envoiPush;

	private ArrayList<File> lstImg;

	public ConnectionTCPThreadVideo(int type, Socket socket, String id,
			ArrayList<File> lstImg, Ihm ihm) throws IOException {
		super(socket, ihm);
		this.type = type;
		this.id = id;
		etat = ETAT_1;
		indexImage = 0;
		this.lstImg = lstImg;
		envoiPush = null;
	}

	@Override
	protected String traiterRequete(String requete) {

		Scanner sc = new Scanner(requete);
		String str;

		switch (etat) {
		case ETAT_1:

			if (!(str = sc.nextLine()).startsWith("GET "))
				return null;
			String idRecu = str.substring(4).trim();

			if (!(str = sc.nextLine()).startsWith("LISTEN_PORT "))
				return null;
			int clientPort = -1;
			try {
				clientPort = Integer.parseInt(str.substring(12).trim());
			} catch (NumberFormatException e) {
				return null;
			}

			if (!sc.nextLine().equals(""))
				return null;

			if (!idRecu.equals(id))
				return null;

			// ok, on ouvre la connection de données

			try {
				socketDonnees = new Socket(getSocket().getInetAddress(),
						clientPort);
				outDonnees = socketDonnees.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			if (type == ConnectionTCPVideo.TYPE_PULL)
				etat = ETAT_2_PULL;
			else if (type == ConnectionTCPVideo.TYPE_PUSH)
				etat = ETAT_2_PUSH;
			
			break;
		case ETAT_2_PULL:
			str = sc.nextLine();

			if (str.startsWith("END")) {
				terminer();
				break;
			}
			
			if (!str.startsWith("GET "))
				return null;

			if (!sc.nextLine().equals(""))
				return null;

			int imageId;
			try {
				imageId = Integer.parseInt(str.substring(4).trim());
			} catch (NumberFormatException e) {
				return null;
			}
			if (imageId == -1) {
				if (!envoyerImage()) {
					return null;
				}
			}
			else {
				if (!envoyerImage(imageId)) {
					return null;
				}
			}

			break;
		case ETAT_2_PUSH :
			str = sc.nextLine();

			if (str.startsWith("END")) {
				if (envoiPush != null) {
					envoiPush.arreter();
					Thread.yield();
				}
				terminer();
				break;
			}
			
			if (!str.startsWith("START"))
				return null;

			if (!sc.nextLine().equals(""))
				return null;
			
			if (envoiPush == null) {
				envoiPush = new ThreadEnvoiPush(this);
				envoiPush.start();
			}
			else {
				envoiPush.reprendre();
			}
			
			etat = ETAT_3_PUSH;
			
			break;
		case ETAT_3_PUSH :
			str = sc.nextLine();

			if (str.startsWith("END")) {
				envoiPush.arreter();
				Thread.yield();
				terminer();
				break;
			}
			
			if (!str.startsWith("PAUSE"))
				return null;

			if (!sc.nextLine().equals(""))
				return null;
			
			envoiPush.mettreEnPause();
			
			etat = ETAT_2_PUSH;
			
			break;
		}

		return null;
	}

	public boolean envoyerImage() {

		File fichierImage = lstImg.get(indexImage);

		byte[] donnees1 = ("0\r\n" + fichierImage.length() + "\r\n").getBytes();
		byte[] donnees2 = null;
		try {
			donnees2 = Flux.lireFichier(fichierImage);
		} catch (FileNotFoundException e1) {
			ihm.afficherErreur("Fichier " + fichierImage.getAbsolutePath()
					+ " introuvable");
			System.exit(1);
		}
		
		try {
			outDonnees.write(donnees1);
			outDonnees.write(donnees2);
			outDonnees.flush();
			indexImage = (indexImage+1)%lstImg.size();
		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(1);
		}

		return true;
	}
	
	private boolean envoyerImage(int id) {
		if (indexImage >= lstImg.size())
			return false;
		
		indexImage = id;
		
		return envoyerImage();
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
