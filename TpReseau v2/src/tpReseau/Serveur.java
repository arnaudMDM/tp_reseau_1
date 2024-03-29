package tpReseau;

import ihm.Ihm;
import ihm.IhmConsole;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import tpReseau.tcp.ConnectionTCPCatalogue;

public class Serveur {

	private static final String ADRESSE_CATALOGUE_DEFAUT = "127.0.0.1";
	private static final int PORT_CATALOGUE_DEFAUT = 5020;
	
	private String adresseCatalogue;
	
	private Ihm ihm;
	private ArrayList<Flux> lstFlux;

	public Serveur(String adresseCatalogue, int portCatalogue, Ihm ihm) {
		
		lstFlux = new ArrayList<Flux>();
		
		this.adresseCatalogue = adresseCatalogue;
		
		this.ihm = ihm;
		
		String catalogue = creerCatalogue(adresseCatalogue, portCatalogue);
		
		ConnectionTCPCatalogue connectionCatalogue = new ConnectionTCPCatalogue(portCatalogue, catalogue, ihm);
		connectionCatalogue.start();
		
		for (Flux flux : lstFlux) {
			if (!flux.demarrer(ihm)) {
				ihm.afficherErreur("Protocole "+flux.getProtocole()+" non support�");
			}
		}
	}

	private String creerCatalogue(String adresseCatalogue, int portCatalogue) {

		analyserFichiers();
		
		String catalogue = "ServerAddress: " + adresseCatalogue + "\r\n";
		catalogue += "ServerPort: " + portCatalogue + "\r\n";

		for (Flux flux : lstFlux) {
			catalogue += "Object ID=" + flux.getId();
			catalogue += " name=" + flux.getNom();
			catalogue += " type=" + flux.getType();
			catalogue += " address=" + flux.getAdresse();
			catalogue += " port=" + flux.getPort();
			catalogue += " protocol=" + flux.getProtocole();
			catalogue += " ips=" + flux.getIps();
			catalogue += "\r\n";
		}
		
		catalogue += "\r\n";
		
		return catalogue;
	}
	
	private void analyserFichiers() {
		File dossier = new File (System.getProperty("user.dir" ));
		File[] tabFile = dossier.listFiles();
		Scanner sc = null;
		String str;
		boolean erreur;
		String idFlux = null;
		String nomFlux = null;
		String typeFlux = null;
		String adresseFlux = null;
		String protocoleFlux = null;
		int portFlux = -1;
		double ipsFlux = 0.0;
		File fileFlux = null;
		ArrayList<File> lstImgFlux;
		
		int etat = 0;

		for (File file : tabFile) {
			if (file.getName().startsWith("flux") && file.getName().endsWith(".txt") && file.canRead()) {
				try {
					sc = new Scanner(file);
				} catch (FileNotFoundException e) {}
				erreur = false;
				etat = 0;
				lstImgFlux = new ArrayList<File>();
				while (sc.hasNextLine() && !erreur) {
					str = sc.nextLine();
					switch (etat) {
					case 0:
						if (str.startsWith("ID: ")) {
							idFlux = str.substring(4);
						}
						else {
							ihm.afficherErreur("Fichier " + file.getAbsolutePath() + " mal form�");
							erreur = true;
						}
						etat++;
						break;
					case 1:
						if (str.startsWith("Name: ")) {
							nomFlux = str.substring(6);
						}
						else {
							ihm.afficherErreur("Fichier " + file.getAbsolutePath() + " mal form�");
							erreur = true;
						}
						etat++;
						break;
					case 2:
						if (str.startsWith("Type: ")) {
							typeFlux = str.substring(6);
							if (typeFlux.equals("JPG")) {
								typeFlux = "JPEG";
							}
							if (!typeFlux.equals("BMP") && !typeFlux.equals("JPEG")) {
								ihm.afficherErreur("Type de flux incorrect dans le fichier " + file.getAbsolutePath());
								erreur = true;
							}
						}
						else {
							ihm.afficherErreur("Fichier " + file.getAbsolutePath() + " mal form�");
							erreur = true;
						}
						etat++;
						break;
					case 3:
						if (str.startsWith("Address: ")) {
							adresseFlux = str.substring(9);
							etat++;
							break;
						}
						else {
							adresseFlux = adresseCatalogue;
							etat++;
						}
					case 4:
						if (str.startsWith("Port: ")) {
							try {
								portFlux = Integer.parseInt(str.substring(6));
							} catch (NumberFormatException nfe) {
								ihm.afficherErreur("Num�ro de port incorrect dans le fichier " + file.getAbsolutePath());
								erreur = true;
							}
						}
						else {
							ihm.afficherErreur("Fichier " + file.getAbsolutePath() + " mal form�");
							erreur = true;
						}
						etat++;
						break;
					case 5:
						if (str.startsWith("Protocol: ")) {
							protocoleFlux = str.substring(10);
							if (!protocoleFlux.equals("TCP_PULL")
									&& !protocoleFlux.equals("TCP_PUSH")
									&& !protocoleFlux.equals("UDP_PULL")
									&& !protocoleFlux.equals("UDP_PUSH")
									&& !protocoleFlux.equals("MCAST_PUSH")) {
								ihm.afficherErreur("Protocole incorrect dans le fichier " + file.getAbsolutePath());
								erreur = true;
							}
							if (protocoleFlux.equals("MCAST_PUSH") && adresseFlux == ADRESSE_CATALOGUE_DEFAUT) {
								ihm.afficherErreur("Multicast impossible sur l'adresse " + adresseFlux);
								erreur = true;
							}
						}
						else {
							ihm.afficherErreur("Fichier " + file.getAbsolutePath() + " mal form�");
							erreur = true;
						}
						etat++;
						break;
					case 6:
						if (str.startsWith("IPS: ")) {
							try {
								ipsFlux = Double.parseDouble(str.substring(5));
							} catch (NumberFormatException nfe) {
								ihm.afficherErreur("Num�ro IPS incorrect dans le fichier " + file.getAbsolutePath());
								erreur = true;
							}
							etat++;
						}
						else {
							ihm.afficherErreur("Fichier " + file.getAbsolutePath() + " mal form�");
							erreur = true;
						}
						break;
					default:
						if ((fileFlux = new File(str)).exists()) {
							lstImgFlux.add(fileFlux);
						}
						else {
							ihm.afficherErreur("Fichier " + file.getAbsolutePath() + " indique une image inexistante : " + str);
							erreur = true;
						}
					}
				}
				if (!erreur) {
					lstFlux.add(new Flux(idFlux, nomFlux, typeFlux, adresseFlux, portFlux, protocoleFlux, ipsFlux, lstImgFlux));
				}
			}
		}
	}

	public static void main(String[] args) {
		
		String adresseCatalogue;
		int portCatalogue;
		
		if (args.length == 1) {
			adresseCatalogue = args[0];
			portCatalogue = PORT_CATALOGUE_DEFAUT;
		}
		else if (args.length > 1) {
			adresseCatalogue = args[0];
			portCatalogue = Integer.parseInt(args[1]);
		}
		else {
			adresseCatalogue = ADRESSE_CATALOGUE_DEFAUT;
			portCatalogue = PORT_CATALOGUE_DEFAUT;
		}
		
		Ihm ihm = new IhmConsole();
		
		new Serveur(adresseCatalogue, portCatalogue, ihm);
	}
}
