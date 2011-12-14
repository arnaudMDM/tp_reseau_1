package tpReseau;

import ihm.Ihm;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConnectionUDPVideoPush extends ConnectionUDPVideo implements EnvoiImage {

	private static final int TIMEOUT_RECEIVE = 60;
	
	private HashMap<ContexteUDP, ThreadEnvoiPush> hmThread;
	
	public ConnectionUDPVideoPush(int port, String id, ArrayList<File> lstImg,
			Ihm ihm) {
		super(port, id, lstImg, ihm);
		hmThread = new HashMap<ContexteUDP, ThreadEnvoiPush>();
	}
	
	private void verifierTemps() {
		Iterator<ContexteUDP> it = lstContexte.iterator();
		
		while (it.hasNext()) {
			ContexteUDP contexte = it.next();
			if (System.currentTimeMillis() - contexte.getTsDerniereRequete() > TIMEOUT_RECEIVE) {
				if (contexte.isEnCours()) {
					hmThread.get(contexte).arreter();
				}
				it.remove();
			}
		}
	}

	@Override
	protected void traiterRequete(String requete, InetAddress adresseExpediteur, int portExpediteur) {
		
		verifierTemps();
		
		System.out.println("Requete : "+requete);
		
		ContexteUDP contexte = chercherContexte(adresseExpediteur, portExpediteur);
		
		contexte.setTsDernierEnvoi(System.currentTimeMillis());
		
		Scanner sc = new Scanner(requete);
		
		try {
			String str = sc.nextLine();
			
			if (str.startsWith("END")) {
				hmThread.get(contexte).arreter();
				lstContexte.remove(contexte);
				return;
			}
			
			if (str.startsWith("ALIVE"))
				return;
			
			if (str.startsWith("START")) {
				if (contexte.isEnCours()) {
					hmThread.get(contexte).reprendre();
					return;
				}
				else {
					ThreadEnvoiPush thr = new ThreadEnvoiPush(this);
					thr.start();
					hmThread.put(contexte, thr);
					contexte.setEncours(true);
				}
			}
				
			if (str.startsWith("PAUSE") && contexte.isEnCours()) {
				hmThread.get(contexte).mettreEnPause();
			}
			
			if (!str.startsWith("GET "))
				return;
			String idRecu = str.substring(4);
			
			if (!idRecu.equals(id))
				return;
			
			str = sc.nextLine();
			
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
		} catch (NoSuchElementException nsee) {
			return;
		}
	}

	@Override
	public boolean envoyerImage() {
		
		return true;
	}
}
