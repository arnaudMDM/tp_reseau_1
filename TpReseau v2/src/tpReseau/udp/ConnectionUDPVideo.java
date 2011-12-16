package tpReseau.udp;

import ihm.Ihm;

import tpReseau.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConnectionUDPVideo extends ConnectionUDP {
	
	protected String id;
	private List<File> lstImg;
	
	protected List<ContexteUDP> lstContexte;
	
	public ConnectionUDPVideo(int port, String id, ArrayList<File> lstImg, Ihm ihm) {
		super(port, ihm);
		
		this.id = id;
		this.lstImg = lstImg;
		
		lstContexte = new ArrayList<ContexteUDP>();
	}
	
	protected ContexteUDP chercherContexte(InetAddress adresse, int port) {
		for (ContexteUDP c : lstContexte) {
			if (c.getAdresseClient().equals(adresse) && c.getPortClient() == port)
				return c;
		}
		
		ContexteUDP contexte = new ContexteUDP(adresse, port);
		lstContexte.add(contexte);
		return contexte;
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
				
				contexte.creerPacketEnvoi(portRecu);
				contexte.setTailleFragment(tailleFragmentRecue);
				contexte.setImgCourante(-1);
			}
		} catch (NoSuchElementException nsee) {
			return;
		}
	}
	
	protected void envoyerImage(ContexteUDP contexte, int id) {
		
		int imgCourante = contexte.getImgCourante();
		int tailleFragment = contexte.getTailleFragment();
		
		if (tailleFragment == 0)
			return;
		
		if (id == -1) {
			contexte.setImgCourante((imgCourante+1)%lstImg.size());
		}
		else {
			imgCourante = id;
		}
		
		int fragmentCourant = 0;
		imgCourante = (imgCourante+1)%lstImg.size();
		int nbFragments;
		if (lstImg.get(imgCourante).length()%tailleFragment == 0)
			nbFragments = (int)lstImg.get(imgCourante).length()/tailleFragment;
		else
			nbFragments = (int)lstImg.get(imgCourante).length()/tailleFragment+1;

		byte[] tabImg = null;
		try {
			tabImg = Flux.lireFichier(lstImg.get(imgCourante));
		} catch (FileNotFoundException e) {
			System.err.println("Fichier " + lstImg.get(imgCourante).getAbsolutePath()
					+ " introuvable");
			System.exit(1);
		}
		
		int tailleFragmentCourant;
		
		while (fragmentCourant < nbFragments) {
			if (fragmentCourant+1 < nbFragments) {
				tailleFragmentCourant = tailleFragment;
			}
			else {
				tailleFragmentCourant = (int)lstImg.get(imgCourante).length()%tailleFragment;
			}
			
			String enTeteStr = Integer.toString(imgCourante) + "\r\n"
					+ lstImg.get(imgCourante).length() + "\r\n" + tailleFragment*fragmentCourant
					+ "\r\n" + tailleFragmentCourant + "\r\n";
			
			byte[] enTete = enTeteStr.getBytes();
			byte[] buffer = new byte[enTete.length + tailleFragmentCourant];
			System.arraycopy(enTete, 0, buffer, 0, enTete.length);
			System.arraycopy(tabImg, tailleFragment*fragmentCourant, buffer, enTete.length, tailleFragmentCourant);
			
			
			DatagramPacket packetEnvoi = contexte.getPacketEnvoi();
			packetEnvoi.setData(buffer, 0, buffer.length);
			try {
				contexte.getSocketEnvoi().send(packetEnvoi);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			fragmentCourant++;
		}
		//System.out.println("Image envoyée");
	}
}
