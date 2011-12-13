package tpReseau;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConnectionUDPVideo extends ConnectionUDP {
	
	private String id;
	private ArrayList<File> lstImg;
	
	private boolean envoiEnCours;
	private int imgCourante;
	private int fragmentCourant;
	private InetAddress adresseClient;
	private int tailleFragment;
	
	private DatagramSocket udpSocketEnvoi;
	private DatagramPacket packetEnvoi;
	
	public ConnectionUDPVideo(int port, String id, ArrayList<File> lstImg, Ihm ihm) {
		super(port, ihm);
		
		this.id = id;
		this.lstImg = lstImg;
		
		envoiEnCours = false;
	}

	@Override
	protected void traiterRequete(String requete, InetAddress adresseExpediteur) {
		Scanner sc = new Scanner(requete);
		
		try {
			String str = sc.nextLine();
			
			if (str.startsWith("END")) {
				envoiEnCours = false;
				return;
			}
			
			if (!str.startsWith("GET "))
				return;
			String idRecu = str.substring(4);
			str = sc.nextLine();
			if (str.equals("")) {
				if (envoiEnCours && adresseExpediteur.equals(adresseClient)) {
					envoyerImage(idRecu);
				}
				else {
					return;
				}
			}
			else {
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
				
				this.adresseClient = adresseExpediteur;
				this.tailleFragment = tailleFragmentRecue;
				
				imgCourante = 0;
				fragmentCourant = 0;
				try {
					udpSocketEnvoi = new DatagramSocket();
				} catch (SocketException e) {
					e.printStackTrace();
				}
				packetEnvoi = new DatagramPacket(null, 0, adresseClient, portRecu);
				envoiEnCours = true;
			}
		} catch (NoSuchElementException nsee) {
			return;
		}
	}
	
	private void envoyerImage(String id) {
		String enTeteStr = Integer.toString(imgCourante) + "\r\n"
				+ lstImg.get(imgCourante).length() + "\r\n" + fragmentCourant
				+ "\r\n" + tailleFragment + "\r\n";
		byte[] enTete = enTeteStr.getBytes();
		
		byte[] buffer = new byte[enTete.length + tailleFragment];
		packetEnvoi.setData(buffer, 0, buffer.length);
		try {
			udpSocketEnvoi.send(packetEnvoi);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
