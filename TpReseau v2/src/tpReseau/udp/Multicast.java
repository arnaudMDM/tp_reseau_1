package tpReseau.udp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import tpReseau.Flux;

public class Multicast extends Thread {
	
	private static final int TAILLE_FRAGMENT = 1024;
	
	private DatagramSocket socket;
	private DatagramPacket paquet;
	private List<File> lstImg;
	
	private int imgCourante;
	private boolean marche;
	private long tpsAttente; // en ms;
	
	public Multicast(InetAddress adresse, int port, List<File> lstImg, double ips) {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
		paquet = new DatagramPacket(new byte[0], 0, adresse, port);
		this.lstImg = lstImg;
		
		tpsAttente = (long) (1000 / ips);
		imgCourante = 0;
		marche = true;
	}
	
	@Override
	public void run() {
		long debut, duree;
		while (marche) {
			debut = System.currentTimeMillis();
			
			envoyerImage();
			
			duree = System.currentTimeMillis() - debut;
			if (tpsAttente > duree) {
				try {
					Thread.sleep(tpsAttente - duree);
				} catch (InterruptedException e) {
				}
			}
		}
		socket.close();
	}
	
	protected void envoyerImage() {
		
		imgCourante =(imgCourante+1)%lstImg.size();
		
		int fragmentCourant = 0;
		imgCourante = (imgCourante+1)%lstImg.size();
		int nbFragments;
		if (lstImg.get(imgCourante).length()%TAILLE_FRAGMENT == 0)
			nbFragments = (int)lstImg.get(imgCourante).length()/TAILLE_FRAGMENT;
		else
			nbFragments = (int)lstImg.get(imgCourante).length()/TAILLE_FRAGMENT+1;
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
				tailleFragmentCourant = TAILLE_FRAGMENT;
			}
			else {
				tailleFragmentCourant = (int)lstImg.get(imgCourante).length()%TAILLE_FRAGMENT;
			}
			
			String enTeteStr = Integer.toString(imgCourante) + "\r\n"
					+ lstImg.get(imgCourante).length() + "\r\n" + TAILLE_FRAGMENT*fragmentCourant
					+ "\r\n" + tailleFragmentCourant + "\r\n";
			
			byte[] enTete = enTeteStr.getBytes();
			byte[] buffer = new byte[enTete.length + tailleFragmentCourant];
			System.arraycopy(enTete, 0, buffer, 0, enTete.length);
			System.arraycopy(tabImg, TAILLE_FRAGMENT*fragmentCourant, buffer, enTete.length, tailleFragmentCourant);
			
			
			paquet.setData(buffer, 0, buffer.length);
			try {
				socket.send(paquet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			fragmentCourant++;
		}
		
		imgCourante =(imgCourante+1)%lstImg.size();
	}
}
