package tpReseau;

import ihm.Ihm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import tpReseau.tcp.ConnectionTCPVideo;
import tpReseau.udp.ConnectionUDPVideo;
import tpReseau.udp.ConnectionUDPVideoPush;

public class Flux {
	
	private String id;
	private String nom;
	private String type;
	private String adresse;
	private int port;
	private String protocole;
	private double ips;
	private ArrayList<File> lstImg;
	
	public Flux(String id, String nom, String type, String adresse, int port,
			String protocole, double ips, ArrayList<File> lstImg) {
		this.id = id;
		this.nom = nom;
		this.type = type;
		this.adresse = adresse;
		this.port = port;
		this.protocole = protocole;
		this.ips = ips;
		this.lstImg = lstImg;
	}

	public String getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}
	
	public String getType() {
		return type;
	}
	
	public String getAdresse() {
		return adresse;
	}

	public int getPort() {
		return port;
	}

	public String getProtocole() {
		return protocole;
	}

	public double getIps() {
		return ips;
	}
	
	boolean demarrer(Ihm ihm) {
		if (protocole.equals("TCP_PULL")) {
			new ConnectionTCPVideo(ConnectionTCPVideo.TYPE_PULL, port, id, lstImg, .0, ihm).start();
		}
		else if (protocole.equals("TCP_PUSH")) {
			new ConnectionTCPVideo(ConnectionTCPVideo.TYPE_PUSH, port, id, lstImg, ips, ihm).start();
		}
		else if (protocole.equals("UDP_PULL")) {
			new ConnectionUDPVideo(port, id, lstImg, ihm).start();
		}
		else if (protocole.equals("UDP_PUSH")) {
			new ConnectionUDPVideoPush(port, id, lstImg, ips, ihm).start();
		}
		else {
			return false;
		}
		
		return true;
	}
	
	public static byte[] lireFichier(File fichier) throws FileNotFoundException {
		BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fichier));

		byte[] donnees = new byte[(int) fichier.length()];
		try {
			fis.read(donnees);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return donnees;
	}
}
