package tpReseau;

import java.io.File;
import java.util.ArrayList;

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
			new ConnectionTCPVideo(port, id, lstImg, ihm).start();
		}
		else if (protocole.equals("UDP_PULL")) {
			new ConnectionUDPVideo(port, id, lstImg, ihm).start();
		}
		else {
			return false;
		}
		
		return true;
	}
	
}
