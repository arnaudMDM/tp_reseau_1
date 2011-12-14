package tpReseau;

import ihm.Ihm;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConnectionUDPVideoPush extends ConnectionUDPVideo {

	private static final int TIMEOUT_RECEIVE = 60;
	
	public ConnectionUDPVideoPush(int port, String id, ArrayList<File> lstImg,
			Ihm ihm) {
		super(port, id, lstImg, ihm);
		timeout = TIMEOUT_RECEIVE;
	}
	
	@Override
	void timeoutDepasse() {
		
	}

	@Override
	protected void traiterRequete(String requete, InetAddress adresseExpediteur, int portExpediteur) {
		
		//System.out.println("Requete : "+requete);
		
		ContexteUDP contexte = chercherContexte(adresseExpediteur, portExpediteur);
		
		Scanner sc = new Scanner(requete);
		
		try {
			String str = sc.nextLine();
			
			if (str.startsWith("END")) {
				lstContexte.remove(contexte);
				return;
			}
			
			if (!str.startsWith("GET "))
				return;
			String idRecu = str.substring(4);
			str = sc.nextLine();
			if (str.equals("")) {
				try {
					envoyerImage(contexte, Integer.parseInt(idRecu));
				} catch (NumberFormatException e) {
					return;
				}
			}
			else {
				if (!idRecu.equals(id))
					return;
				
				if (!str.startsWith("LISTEN_PORT "))
					return;
				int portRecu;
				try {
					portRecu = Integer.parseInt(str.substring(12).trim());
				} catch (NumberFormatException nfe) {
					return;
				}
				
				contexte.creerPacketEnvoi(portRecu);
				
				str = sc.nextLine();
				if (!str.startsWith("FRAGMENT_SIZE "))
					return;
				int tailleFragmentRecue;
				try {
					tailleFragmentRecue = Integer.parseInt(str.substring(14).trim());
				} catch (NumberFormatException nfe) {
					return;
				}
				
				str = sc.nextLine();
				if (!str.equals(""))
					return;
				
				contexte.setTailleFragment(tailleFragmentRecue);
				contexte.setImgCourante(-1);
			}
		} catch (NoSuchElementException nsee) {
			return;
		}
	}
}
