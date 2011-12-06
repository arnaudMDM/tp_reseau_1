package tpReseau;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ConnectionTCPThreadVideo extends ConnectionTCPThread {

	private static final int ETAT_1 = 0, ETAT_2 = 1;
	
	private String id;
	private int etat;
	private int dernierImageId;
	
	private Socket socketDonnees;
	private OutputStream outDonnees;
	
	private ArrayList<File> lstImg;

	public ConnectionTCPThreadVideo(Socket socket, String id, ArrayList<File> lstImg) throws IOException {
		super(socket);
		this.id = id;
		etat = ETAT_1;
		dernierImageId = 1;
		this.lstImg = lstImg;
	}

	@Override
	protected String traiterRequete(String requete) {
//		System.out.println("Requ�te : "+requete);
		
		Scanner sc = new Scanner(requete);
		String str;
		
		switch (etat) {
		case ETAT_1 :
			
			if (!(str = sc.nextLine()).startsWith("GET "))
				return null;
			String idRecu = str.substring(4).trim();
			
			//System.out.println("ID re�u : " + idRecu);
	
			if (!(str = sc.nextLine()).startsWith("LISTEN_PORT "))
				return null;
			int clientPort = -1;
			try {
				clientPort = Integer.parseInt(str.substring(12).trim());
			} catch (NumberFormatException e) {
				return null;
			}
	
			//System.out.println("clientPort re�u : " + clientPort);
			
			if (!sc.nextLine().equals(""))
				return null;
			
			if (!idRecu.equals(id))
				return null;
	
			// ok, on ouvre la connection de donn�es
			
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
			
			if (str.startsWith("END")){
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
				imageId = dernierImageId;
			}
			
			if (!envoyerImage(imageId)) {
				return null;
			}
			dernierImageId = imageId;
			
			break;
			
		}

		return null;
	}
	
	private boolean envoyerImage(int imageId) {
		
		FileInputStream fis = null;
		File fichierImage = null;
		//System.out.println("imageId = "+imageId);
		if (imageId <= lstImg.size() && imageId > 0) {
			fichierImage = lstImg.get(imageId-1);
		}
		else {
			return false;
		}
		try {
			fis = new FileInputStream(fichierImage);
		} catch (FileNotFoundException e1) {
			System.err.println("Fichier "+fichierImage.getAbsolutePath()+" introuvable");
			System.exit(1);
		}
		
		int octet = 0;
		int i = 0;
		byte[] donnees1 = ("0\r\n"+fichierImage.length()+"\r\n").getBytes();
		byte[] donnees2 = new byte[(int)fichierImage.length()];
		
		try {
			octet = fis.read();
			while (octet != -1) {
				donnees2[i] = (byte)octet;
				octet = fis.read();
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		try {
			fis.close();
			outDonnees.write(donnees1);
			outDonnees.write(donnees2);
			outDonnees.flush();
			//System.out.println("Fichier "+fichierImage.getName() + " envoy� !");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return true;
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
